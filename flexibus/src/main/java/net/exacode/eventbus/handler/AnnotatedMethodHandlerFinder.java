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
package net.exacode.eventbus.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.exacode.eventbus.exception.ExceptionHandler;
import net.exacode.eventbus.exception.ExceptionLoggingHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link MethodHandlerFinder} for collecting all event handler methods that
 * are marked with an appropriate annotation.
 * 
 * @author Paweł Mendelski
 */
public class AnnotatedMethodHandlerFinder<A extends Annotation> implements
		MethodHandlerFinder {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Class<A> annotationType;

	private final ExceptionHandler exceptionHandler;

	public AnnotatedMethodHandlerFinder(Class<A> annotationType) {
		this(annotationType, new ExceptionLoggingHandler());
	}

	public AnnotatedMethodHandlerFinder(Class<A> annotationType,
			ExceptionHandler exceptionHandler) {
		this.annotationType = annotationType;
		this.exceptionHandler = exceptionHandler;
	}

	@Override
	public Map<Class<?>, Set<MethodHandler>> findHandlerMethods(Object listener) {
		Map<Class<?>, Set<MethodHandler>> methodsInListener = new HashMap<Class<?>, Set<MethodHandler>>();
		Class<?> clazz = listener.getClass();

		for (Method method : clazz.getMethods()) {
			A eventHandlerAnnotation = method.getAnnotation(annotationType);
			if (eventHandlerAnnotation != null) {
				Class<?>[] parameterTypes = method.getParameterTypes();
				if (parameterTypes.length != 1) {
					throw new IllegalArgumentException(
							"Method "
									+ method
									+ " has @EventHandler annotation, but requires "
									+ parameterTypes.length
									+ " arguments.  Event handler methods must require a single argument.");
				}
				Class<?> eventType = parameterTypes[0];
				if (eventType.isPrimitive()) {
					eventType = Primitives.wrap(eventType);
				}
				MethodHandler handler = new SimpleHandlerMethod(listener,
						method, exceptionHandler);

				Set<MethodHandler> handlers = methodsInListener.get(eventType);
				if (handlers == null) {
					handlers = new HashSet<MethodHandler>();
					methodsInListener.put(eventType, handlers);
				}
				handlers.add(handler);
				logger.trace(
						"Connected handler with event.\nHandler: {}\nEvent: {}",
						handler, eventType);
			}
		}
		return methodsInListener;
	}

}
