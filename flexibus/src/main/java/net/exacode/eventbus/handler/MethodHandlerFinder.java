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

import java.util.Map;
import java.util.Set;

import net.exacode.eventbus.EventBus;

/**
 * A method for finding event handler methods in objects, for use by
 * {@link EventBus}.
 * 
 * @author mendlik
 */
public interface MethodHandlerFinder {

	/**
	 * Finds all suitable event handler methods in {@code handler}, organizes
	 * them by the type of event they handle, and wraps them in
	 * {@link MethodHandler}s.
	 * 
	 * @param handler
	 *            object whose methods are handle events.
	 */
	Map<Class<?>, Set<MethodHandler>> findHandlerMethods(Object handler);

}