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

import java.util.Set;

import net.exacode.eventbus.builder.EventBusBuilder;
import net.exacode.eventbus.dispatch.DispatchStrategy;
import net.exacode.eventbus.dispatch.SyncDispatchStrategy;
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
 * {@link DispatchStrategy}</li>
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

	private final String id;

	private final HandlerRegistry handlerRegistry;

	private final DispatchStrategy dispatchStrategy;

	public static EventBusBuilder builder() {
		return new EventBusBuilder();
	}

	public EventBus() {
		this.id = EventBus.class.getSimpleName();
		this.handlerRegistry = new HandlerRegistry(
				new AnnotatedMethodHandlerFinder<EventHandler>(
						EventHandler.class));
		this.dispatchStrategy = new SyncDispatchStrategy();
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
			dispatchStrategy.dispatchEvent(event, handlerMethods);
		}

		if (!dispatched && !(event instanceof DeadEvent)) {
			post(new DeadEvent(event));
		}
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
	 * @throws IllegalArgumentException
	 *             if the object was not previously registered.
	 */
	public void unregister(Object handler) {
		handlerRegistry.removeHandler(handler);
	}

	@Override
	public String toString() {
		return "EventBus [id=" + id + "]";
	}

}
