/*
 * Copyright (C) 2007 The Guava Authors
 * Copyright (C) 2007 mendlik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.exacode.eventbus;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import net.exacode.eventbus.builder.EventBusBuilder;
import net.exacode.eventbus.dispatch.DispatchStrategy;
import net.exacode.eventbus.dispatch.EventHandlerDispatchStrategy;
import net.exacode.eventbus.dispatch.concurrent.SyncDispatchStrategy;
import net.exacode.eventbus.handler.AnnotatedMethodHandlerFinder;
import net.exacode.eventbus.handler.MethodHandler;
import net.exacode.eventbus.handler.MethodHandlerFinder;

/**
 * Dispatches events to listeners, and provides ways for listeners to register
 * themselves.
 * 
 * <p>
 * This EventBus is based on event bus from guava
 * (http://code.google.com/p/guava-libraries/wiki/EventBusExplained).
 * Modifications opens it for <a href=
 * "https://code.google.com/p/guava-libraries/wiki/EventBusExplained#Why_can't_I_do_<magic_thing>_with_EventBus_?"
 * >magic things</a> by providing appropriate strategies
 * {@code Strategy Pattern}.
 * 
 * <h2>Guava EventBus modifications</h2>
 * <ol>
 * <li>Does not depend on guava library</li>
 * <li>Available usage of primitive parameters in handler methods</li>
 * <li>Uses strategy pattern for event dispatching: {@link DispatchStrategy}</li>
 * <li>Uses strategy pattern for finding handler methods:
 * {@link MethodHandlerFinder}</li>
 * <li>Can be easily configured and build with {@link EventBusBuilder}</li>
 * </ol>
 * 
 * <h2>Receiving Events</h2> To receive events, an object should:
 * <ol>
 * <li>Expose a public method, known as the <i>event handler method</i>, which
 * accepts a single argument of the type of event desired;</li>
 * <li>Mark it with a {@link EventHandler} annotation (Annotation can be changed
 * by changing {@link MethodHandlerFinder});</li>
 * <li>Pass itself to an EventBus instance's {@link #register(Object)} method.</li>
 * </ol>
 * 
 * <h2>Posting Events</h2> To post an event, simply provide the event object to
 * the {@link #post(Object)} method. The EventBus instance will determine the
 * type of event and route it to all registered listeners.
 * 
 * <p>
 * Events are routed based on their type &mdash; an event will be delivered to
 * any handler for any type to which the event is <em>assignable.</em> This
 * includes implemented interfaces, all superclasses, and all interfaces
 * implemented by superclasses.
 * 
 * <p>
 * 
 * <h2>Handler Methods</h2> Event handler methods must accept only one argument:
 * the event.
 * 
 * <h2>Dead Events</h2>
 * If an event is posted, but no registered handlers can accept it, it is
 * considered "dead." To give the system a second chance to handle dead events,
 * they are wrapped in an instance of {@link DeadEvent} and reposted.
 * 
 * <p>
 * If a handler for a supertype of all events (such as Object) is registered, no
 * event will ever be considered dead, and no DeadEvents will be generated.
 * Accordingly, while DeadEvent extends {@link Object}, a handler registered to
 * receive any Object will never receive a DeadEvent.
 * 
 * <h2>Concurrency</h2> This class is safe for concurrent use - you can use it
 * in different threads.
 * <p>
 * If you want to take control over sync/async execution of event handlers.
 * Provide appropriate {@link DispatchStrategy}.
 * 
 * <h2>Default strategies</h2>
 * <ol>
 * <li>Uses {@link SyncDispatchStrategy}. Events are dispatched synchronously.
 * Exceptions are propagated as in normal method invocation.</li>
 * <li>For finding handler methods responsible is
 * {@link AnnotatedMethodHandlerFinder}. This finder looks for annotation
 * {@link EventHandler} or annotation that is annotated with it.</li>
 * </ol>
 * 
 * @author Cliff Biffle
 * @author mendlik
 * 
 */
public class EventBus {

	/** simple struct representing an event and it's subscriber */
	private static class HandlerWithEvent {
		final Object event;
		final Collection<MethodHandler> handlers;
		final DispatchStrategy dispatchStrategy;

		public HandlerWithEvent(Object event,
				Collection<MethodHandler> handlers,
				DispatchStrategy dispatchStrategy) {
			this.event = event;
			this.handlers = handlers;
			this.dispatchStrategy = dispatchStrategy;
		}

		public void execute() {
			dispatchStrategy.dispatchEvent(event, handlers);
		}
	}

	public static EventBusBuilder builder() {
		return new EventBusBuilder();
	}

	private final String id;

	private final HandlerRegistry handlerRegistry;

	private final DispatchStrategy dispatchStrategy;

	/** true if the current thread is currently dispatching an event */
	private final ThreadLocal<Boolean> isDispatching = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return false;
		}
	};

	/** queues of events for the current thread to dispatch */
	private final ThreadLocal<Queue<HandlerWithEvent>> eventsToDispatch = new ThreadLocal<Queue<HandlerWithEvent>>() {
		@Override
		protected Queue<HandlerWithEvent> initialValue() {
			return new LinkedList<HandlerWithEvent>();
		}
	};

	public EventBus() {
		this.id = EventBus.class.getSimpleName();
		this.handlerRegistry = new HandlerRegistry(
				new AnnotatedMethodHandlerFinder<EventHandler>(
						EventHandler.class));
		this.dispatchStrategy = new EventHandlerDispatchStrategy();
	}

	public EventBus(String logId, MethodHandlerFinder methodHandlerFinder,
			DispatchStrategy eventDispatchStrategy) {
		this.id = logId;
		this.handlerRegistry = new HandlerRegistry(methodHandlerFinder);
		this.dispatchStrategy = eventDispatchStrategy;
	}

	/**
	 * Posts an event to all registered handlers. This method will return
	 * successfully after the event has been posted to all handlers.
	 * 
	 * <p>
	 * Exception handling, asynchronous invocation and others parameters depend
	 * on {@link DispatchStrategy}. Default {@link DispatchStrategy} is set to
	 * {@link SyncDispatchStrategy}.
	 * 
	 * 
	 * <p>
	 * If no handlers have been subscribed for {@code event}'s class, and
	 * {@code event} is not already a {@link DeadEvent}, it will be wrapped in a
	 * DeadEvent and reposted.
	 * 
	 * <p>
	 * Null events are skipped.
	 * 
	 * @param event
	 *            event to post.
	 */
	public void post(Object event) {
		if (event == null) {
			return;
		}
		Set<MethodHandler> handlerMethods = handlerRegistry
				.findEventHandlerMethods(event.getClass());
		boolean dispatched = false;
		if (handlerMethods != null && !handlerMethods.isEmpty()) {
			dispatched = true;
			enqueueEventExecution(handlerMethods, event);
		}

		if (!dispatched && !(event instanceof DeadEvent)) {
			post(new DeadEvent(event));
		}
		dispatchQueuedEvents();
	}

	/**
	 * Registers all handler methods on {@code object} to receive events.
	 * Handler methods are selected and classified using this EventBus's
	 * {@link MethodHandlerFinder}; the default strategy is set to the
	 * {@link AnnotatedHandlerFinder} that looks for {@link EventHandler}
	 * annotation.
	 * 
	 * <p>
	 * primitive parameters are automatically wrapped.
	 * 
	 * @param object
	 *            object whose handler methods should be registered.
	 */
	public void register(Object handler) {
		handlerRegistry.addHandler(handler);
	}

	/**
	 * Unregisters all handler methods on a registered {@code object}.
	 * 
	 * @param object
	 *            object whose handler methods should be unregistered.
	 */
	public void unregister(Object handler) {
		handlerRegistry.removeHandler(handler);
	}

	@Override
	public String toString() {
		return "EventBus [id=" + id + "]";
	}

	/**
	 * Queue the {@code event} for dispatch during
	 * {@link #dispatchQueuedEvents()}. Events are queued in-order of occurrence
	 * so they can be dispatched in the same order.
	 */
	private void enqueueEventExecution(Collection<MethodHandler> handlers,
			Object event) {
		eventsToDispatch.get().offer(
				new HandlerWithEvent(event, handlers, dispatchStrategy));
	}

	/**
	 * Drain the queue of events to be dispatched. As the queue is being
	 * drained, new events may be posted to the end of the queue.
	 */
	private void dispatchQueuedEvents() {
		// don't dispatch if we're already dispatching, that would allow
		// reentrancy
		// and out-of-order events. Instead, leave the events to be dispatched
		// after the in-progress dispatch is complete.
		if (isDispatching.get()) {
			return;
		}

		isDispatching.set(true);
		try {
			Queue<HandlerWithEvent> events = eventsToDispatch.get();
			HandlerWithEvent hadnlerWithEvent;
			while ((hadnlerWithEvent = events.poll()) != null) {
				hadnlerWithEvent.execute();
			}
		} finally {
			isDispatching.remove();
			eventsToDispatch.remove();
		}
	}

}
