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

import java.util.Arrays;
import java.util.Collection;

import net.exacode.eventbus.dispatch.DispatchStrategy;
import net.exacode.eventbus.handler.MethodHandler;

/**
 * Filters event handlers so only one registered handler will receive published
 * event. Best fitted (event type) handler is used.
 * 
 * 
 * @author mendlik
 * 
 */
public class EventBusUniqueDispatchStrategy implements DispatchStrategy {

	private final DispatchStrategy dispatchStrategy;

	public EventBusUniqueDispatchStrategy(DispatchStrategy dispatchStrategy) {
		this.dispatchStrategy = dispatchStrategy;
	}

	@Override
	public void dispatchEvent(Object event,
			Collection<MethodHandler> handlerMethods) {
		for (MethodHandler methodHandler : handlerMethods) {
			dispatchStrategy.dispatchEvent(event, Arrays.asList(methodHandler));
			break;
		}
	}
}
