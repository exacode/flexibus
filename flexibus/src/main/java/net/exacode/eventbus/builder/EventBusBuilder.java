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
package net.exacode.eventbus.builder;

import java.lang.annotation.Annotation;

import net.exacode.eventbus.EventBus;
import net.exacode.eventbus.EventHandler;
import net.exacode.eventbus.dispatch.DispatchStrategy;
import net.exacode.eventbus.dispatch.EventHandlerDispatchStrategy;
import net.exacode.eventbus.exception.ExceptionHandler;
import net.exacode.eventbus.exception.LoggingExceptionHandler;
import net.exacode.eventbus.handler.AnnotatedMethodHandlerFinder;
import net.exacode.eventbus.handler.MethodHandlerFinder;

/**
 * {@link EventBus} builder.
 * 
 * @author mendlik
 * 
 */
public class EventBusBuilder {

	private DispatchStrategy eventDispatchStrategy;

	private MethodHandlerFinder methodHandlerFindingStrategy;

	private String logId;

	private DeadEventLogHandler deadEventLogHandler;

	private ExceptionHandler exceptionHandler;

	public EventBusBuilder eventDispatchStrategy(
			DispatchStrategy eventDispatchStrategy) {
		this.eventDispatchStrategy = eventDispatchStrategy;
		return this;
	}

	public EventBusBuilder methodHandlerFindingStrategy(
			MethodHandlerFinder methodHandlerFindingStrategy) {
		this.methodHandlerFindingStrategy = methodHandlerFindingStrategy;
		return this;
	}

	public EventBusBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
		return this;
	}

	public EventBusBuilder logId(String logId) {
		this.logId = logId;
		return this;
	}

	public EventBusBuilder withLoggingExceptionHandler() {
		this.exceptionHandler = new LoggingExceptionHandler();
		return this;
	}

	public EventBusBuilder withDeadEventLogHandler() {
		this.deadEventLogHandler = new DeadEventLogHandler();
		return this;
	}

	public <A extends Annotation> EventBusBuilder annotatedMethodHandlerFindingStrategy(
			Class<A> annotationType) {
		this.methodHandlerFindingStrategy = new AnnotatedMethodHandlerFinder<A>(
				annotationType);
		return this;
	}

	/**
	 * Builds {@link EventBus} from components.
	 * 
	 * @return new instance of {@link EventBus}
	 */
	public EventBus buildEventBus(Object... handlers) {
		if (methodHandlerFindingStrategy == null) {
			if (exceptionHandler != null) {
				methodHandlerFindingStrategy = new AnnotatedMethodHandlerFinder<EventHandler>(
						EventHandler.class, exceptionHandler);
			} else {
				methodHandlerFindingStrategy = new AnnotatedMethodHandlerFinder<EventHandler>(
						EventHandler.class);
			}
		}
		if (eventDispatchStrategy == null) {
			eventDispatchStrategy = new EventHandlerDispatchStrategy();
		}
		if (logId == null) {
			logId = "EventBus";
		}
		EventBus eventBus = new EventBus(logId, methodHandlerFindingStrategy,
				eventDispatchStrategy);
		if (deadEventLogHandler != null) {
			eventBus.register(deadEventLogHandler);
		}
		for (Object handler : handlers) {
			eventBus.register(handler);
		}
		return eventBus;
	}
}
