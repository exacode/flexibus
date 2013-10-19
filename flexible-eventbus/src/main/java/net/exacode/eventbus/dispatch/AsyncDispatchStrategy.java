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
package net.exacode.eventbus.dispatch;

import java.util.Collection;

import net.exacode.eventbus.handler.MethodHandler;

/**
 * Enables asynchronous events dispatching. Decorates other
 * {@link DispatchStrategy}.
 * <p>
 * 
 * If exception (Throwable) occurs during event dispatching then
 * {@link AsyncInvocationExceptionEvent} is published in source eventBus.
 * 
 * @author mendlik
 * 
 */
public class AsyncDispatchStrategy implements DispatchStrategy {

	static public interface AsyncEventDescriptor {
		boolean isAsync(Object event);
	}

	@SuppressWarnings("serial")
	static public class AsyncInvocationExceptionEvent extends RuntimeException {

		private final Throwable cause;

		private Object event;

		public AsyncInvocationExceptionEvent(Throwable cause, Object event) {
			this.cause = cause;
			this.event = event;
		}

		@Override
		public Throwable getCause() {
			return cause;
		}

		protected Object getEvent() {
			return event;
		}

		protected void setEvent(Object event) {
			this.event = event;
		}

	}

	public static final AsyncEventDescriptor DEFAULT_EVENT_DESCRIPTOR = new AsyncEventDescriptor() {

		@Override
		public boolean isAsync(Object event) {
			return true;
		}

	};

	public AsyncEventDescriptor eventDescriptor = DEFAULT_EVENT_DESCRIPTOR;

	public AsyncDispatchStrategy(AsyncEventDescriptor eventDescriptor) {
		this.eventDescriptor = eventDescriptor;
	}

	public AsyncDispatchStrategy() {
	}

	@Override
	public void dispatchEvent(Object event,
			Collection<MethodHandler> handlerMethods) {
		if (eventDescriptor.isAsync(event)
				&& !(event instanceof AsyncInvocationExceptionEvent)) {
			asyncDispatch(event, handlerMethods);
		} else {
			syncDispatch(event, handlerMethods);
		}
	}

	private void asyncDispatch(final Object event,
			Collection<MethodHandler> handlerMethods) {
		for (final MethodHandler methodHandler : handlerMethods) {
			Thread eventDispatchThread = new Thread() {
				@Override
				public void run() {
					methodHandler.handleEvent(event);
				}
			};
			eventDispatchThread.setPriority(Thread.MIN_PRIORITY);
			eventDispatchThread.start();
		}
	}

	private void syncDispatch(Object event,
			Collection<MethodHandler> handlerMethods) {
		for (MethodHandler wrapper : handlerMethods) {
			wrapper.handleEvent(event);
		}
	}

}
