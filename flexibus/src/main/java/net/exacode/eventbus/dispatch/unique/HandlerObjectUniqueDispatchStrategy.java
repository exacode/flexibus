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
package net.exacode.eventbus.dispatch.unique;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.exacode.eventbus.dispatch.DispatchStrategy;
import net.exacode.eventbus.dispatch.concurrent.SyncDispatchStrategy;
import net.exacode.eventbus.handler.MethodHandler;

/**
 * Filters event handling methods in one handler object so only one registered
 * handling method will receive published event. Best fitted (event type)
 * methods are used.
 * 
 * 
 * @author mendlik
 * 
 */
public class HandlerObjectUniqueDispatchStrategy implements DispatchStrategy {

	private final DispatchStrategy dispatchStrategy;

	public HandlerObjectUniqueDispatchStrategy(DispatchStrategy dispatchStrategy) {
		this.dispatchStrategy = dispatchStrategy;
	}

	public HandlerObjectUniqueDispatchStrategy() {
		this.dispatchStrategy = new SyncDispatchStrategy();
	}

	@Override
	public void dispatchEvent(Object event,
			Collection<MethodHandler> handlerMethods) {
		Set<Object> handlerObjects = new HashSet<Object>();
		List<MethodHandler> filteredHandlers = new ArrayList<MethodHandler>(
				handlerMethods.size());
		for (MethodHandler methodHandler : handlerMethods) {
			if (!handlerObjects.contains(methodHandler.getTarget())) {
				handlerObjects.add(methodHandler.getTarget());
				filteredHandlers.add(methodHandler);
			}
		}
		dispatchStrategy.dispatchEvent(event, filteredHandlers);
	}
}
