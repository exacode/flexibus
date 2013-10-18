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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.exacode.eventbus.handler.MethodHandler;
import net.exacode.eventbus.handler.MethodHandlerFinder;

/**
 * Stores and organizes {@link MethodHandler}s.
 * <p>
 * Thread safe.
 * 
 * @author mendlik
 * 
 */
class HandlerRegistry {

	private final Map<Class<?>, Set<MethodHandler>> eventHandlerMethods = new HashMap<Class<?>, Set<MethodHandler>>();

	private final MethodHandlerFinder finder;

	private final Map<Class<?>, Set<Class<?>>> flattenHierarchyCache = new WeakHashMap<Class<?>, Set<Class<?>>>();

	private final ReadWriteLock eventHandlerMethodsLock = new ReentrantReadWriteLock();

	public HandlerRegistry(MethodHandlerFinder finder) {
		this.finder = finder;
	}

	/**
	 * Registers all subscriber methods on {@code object} to receive events.
	 * Subscriber methods are selected and classified using this EventBus's
	 * {@link SubscriberFindingStrategy}; the default strategy is the
	 * {@link AnnotatedSubscriberFinder}.
	 * 
	 * @param handler
	 *            object whose subscriber methods should be registered.
	 */
	public void addHandler(Object handler) {
		Map<Class<?>, Set<MethodHandler>> handlerMethods = finder
				.findHandlerMethods(handler);
		eventHandlerMethodsLock.writeLock().lock();
		try {
			mergeEventHadnlerMethods(handlerMethods);
		} finally {
			eventHandlerMethodsLock.writeLock().unlock();
		}
	}

	/**
	 * Removes {@link MethodHandler}s extracted from handler object.
	 * 
	 * @param handler
	 */
	public void removeHandler(Object handler) {
		Map<Class<?>, Set<MethodHandler>> methodsInListener = finder
				.findHandlerMethods(handler);
		for (Entry<Class<?>, Set<MethodHandler>> entry : methodsInListener
				.entrySet()) {
			Class<?> eventType = entry.getKey();
			Set<MethodHandler> eventMethodsInListener = entry.getValue();

			eventHandlerMethodsLock.writeLock().lock();
			try {
				Set<MethodHandler> currentHandlers = eventHandlerMethods
						.get(eventType);
				if (currentHandlers == null
						|| !currentHandlers.containsAll(entry.getValue())) {
					throw new IllegalArgumentException(
							"Missing event handler for an annotated method. Is "
									+ handler + " registered?");
				}
				currentHandlers.removeAll(eventMethodsInListener);
			} finally {
				eventHandlerMethodsLock.writeLock().unlock();
			}
		}
	}

	/**
	 * Finds all {@link MethodHandler}s connected with given {@code eventType}.
	 * 
	 * @param eventType
	 * @return handlerMethods
	 */
	public Set<MethodHandler> findEventHandlerMethods(Class<?> eventType) {
		Set<MethodHandler> handlerMethods = new HashSet<MethodHandler>();
		Set<Class<?>> eventFlattenedTypes = flattenEventHierarchy(eventType);
		for (Class<?> eventFlattenedType : eventFlattenedTypes) {
			Set<MethodHandler> handlers = eventHandlerMethods
					.get(eventFlattenedType);
			if (handlers != null) {
				handlerMethods.addAll(handlers);
			}
		}
		return handlerMethods;
	}

	private void mergeEventHadnlerMethods(
			Map<Class<?>, Set<MethodHandler>> handlerMethods) {
		for (Entry<Class<?>, Set<MethodHandler>> entry : handlerMethods
				.entrySet()) {
			Set<MethodHandler> handlerSet = eventHandlerMethods.get(entry
					.getKey());
			if (handlerSet == null) {
				handlerSet = new HashSet<MethodHandler>();
				eventHandlerMethods.put(entry.getKey(), handlerSet);
			}
			handlerSet.addAll(entry.getValue());
		}
	}

	private Set<Class<?>> flattenEventHierarchy(Class<?> concreteClass) {
		Set<Class<?>> hierarchy = flattenHierarchyCache.get(concreteClass);
		if (hierarchy == null) {
			synchronized (flattenHierarchyCache) {
				hierarchy = new HashSet<Class<?>>();
				Class<?> currentClass = concreteClass;
				while (currentClass != null) {
					hierarchy.add(currentClass);
					hierarchy
							.addAll(Arrays.asList(currentClass.getInterfaces()));
					currentClass = currentClass.getSuperclass();
				}
				flattenHierarchyCache.put(concreteClass, hierarchy);
			}
		}
		return hierarchy;
	}
}
