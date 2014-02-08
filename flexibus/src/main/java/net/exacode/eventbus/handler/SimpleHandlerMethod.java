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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.exacode.eventbus.exception.ExceptionHandler;

/**
 * Represents simple handler method.
 * 
 * @author mendlik
 * 
 */
public class SimpleHandlerMethod implements MethodHandler {

	/**
	 * Handler object.
	 */
	private final Object target;

	/**
	 * Handler method.
	 */
	private final Method method;

	private final ExceptionHandler exceptionHandler;

	/**
	 * Creates a new EventHandler to wrap {@code method} on @{code target}.
	 * 
	 * @param target
	 *            object to which the method applies.
	 * @param method
	 *            handler method.
	 */
	public SimpleHandlerMethod(Object target, Method method,
			ExceptionHandler exceptionHandler) {
		assert target != null : "EventHandler target cannot be null.";
		assert method != null : "EventHandler method cannot be null.";
		assert exceptionHandler != null : "EventHandler exceptionHandler cannot be null.";

		this.target = target;
		this.method = method;
		this.exceptionHandler = exceptionHandler;
		method.setAccessible(true);
	}

	/**
	 * 
	 * @return handler object
	 */
	@Override
	public Object getTarget() {
		return target;
	}

	/**
	 * 
	 * @return handler method
	 */
	@Override
	public Method getMethod() {
		return method;
	}

	/**
	 * Invokes the wrapped handler method to handle an {@code event}.
	 * 
	 * @param event
	 *            event to handle
	 */
	@Override
	public void handleEvent(Object event) {
		try {
			method.invoke(target, new Object[] { event });
		} catch (IllegalAccessException e) {
			exceptionHandler.handle(new IllegalArgumentException(
					"Method became inaccessible: " + event, e));
		} catch (InvocationTargetException e) {
			exceptionHandler.handle(e.getCause());
		} catch (Throwable e) {
			exceptionHandler.handle(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleHandlerMethod other = (SimpleHandlerMethod) obj;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MethodHandler [target=" + target + ", method=" + method + "]";
	}

}
