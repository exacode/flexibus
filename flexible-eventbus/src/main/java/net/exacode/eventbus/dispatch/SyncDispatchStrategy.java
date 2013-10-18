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

import java.util.Set;

import net.exacode.eventbus.EventBus;
import net.exacode.eventbus.handler.MethodHandler;

/**
 * Dispatches events in synchronous way.
 * <p>
 * It is default {@link DispatchStrategy} used by {@link EventBus}.
 * 
 * @author mendlik
 * 
 */
public class SyncDispatchStrategy implements DispatchStrategy {

	@Override
	public void dispatchEvent(Object event, Set<MethodHandler> handlerMethods) {
		for (MethodHandler wrapper : handlerMethods) {
			wrapper.handleEvent(event);
		}
	}

}
