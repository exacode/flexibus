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
 * Checks if passed event has not more than one handler connected.
 * <p>
 * Otherwise throws {@link IllegalStateException}
 * 
 * 
 * @author mendlik
 * 
 */
public class UniqueDispatchStrategy implements DispatchStrategy {

	static public interface UniqueEventDescriptor {
		boolean isUnique(Object event);
	}

	public static final UniqueEventDescriptor DEFAULT_EVENT_DESCRIPTOR = new UniqueEventDescriptor() {

		@Override
		public boolean isUnique(Object event) {
			return true;
		}

	};

	public UniqueEventDescriptor eventDescriptor = DEFAULT_EVENT_DESCRIPTOR;

	private DispatchStrategy dispatchStrategy = new SyncDispatchStrategy();

	public UniqueDispatchStrategy(UniqueEventDescriptor eventDescriptor,
			DispatchStrategy dispatchStrategy) {
		this.eventDescriptor = eventDescriptor;
		this.dispatchStrategy = dispatchStrategy;
	}

	public UniqueDispatchStrategy(UniqueEventDescriptor eventDescriptor) {
		this.eventDescriptor = eventDescriptor;
	}

	public UniqueDispatchStrategy(DispatchStrategy dispatchStrategy) {
		this.dispatchStrategy = dispatchStrategy;
	}

	public UniqueDispatchStrategy() {
	}

	@Override
	public void dispatchEvent(Object event,
			Collection<MethodHandler> handlerMethods) {
		if (handlerMethods.size() > 1 && eventDescriptor.isUnique(event)) {
			throw new IllegalStateException(
					"More than one handlers subscribed to unique event. \nEvent: "
							+ event + "\nHandlers: " + handlerMethods);
		}
		dispatchStrategy.dispatchEvent(event, handlerMethods);
	}
}
