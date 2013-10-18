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

import net.exacode.eventbus.dispatch.AsyncDispatchStrategy;
import net.exacode.eventbus.dispatch.AsyncDispatchStrategy.AsyncEventDescriptor;
import net.exacode.eventbus.dispatch.DispatchStrategy;
import net.exacode.eventbus.dispatch.SyncDispatchStrategy;
import net.exacode.eventbus.dispatch.UniqueDispatchStrategy;
import net.exacode.eventbus.dispatch.UniqueDispatchStrategy.UniqueEventDescriptor;

public class EventDispatchStrategyBuilder {
	private AsyncEventDescriptor asyncEventDescriptor;
	private UniqueEventDescriptor uniqueEventDescriptor;

	private final EventBusBuilder eventBusBuilder;

	EventDispatchStrategyBuilder(EventBusBuilder eventBusBuilder) {
		this.eventBusBuilder = eventBusBuilder;
	}

	public EventDispatchStrategyBuilder async() {
		asyncEventDescriptor = AsyncDispatchStrategy.DEFAULT_EVENT_DESCRIPTOR;
		return this;
	}

	public EventDispatchStrategyBuilder async(
			AsyncEventDescriptor eventDescriptor) {
		asyncEventDescriptor = eventDescriptor;
		return this;
	}

	public EventDispatchStrategyBuilder unique() {
		uniqueEventDescriptor = UniqueDispatchStrategy.DEFAULT_EVENT_DESCRIPTOR;
		return this;
	}

	public EventDispatchStrategyBuilder unique(
			UniqueEventDescriptor eventDescriptor) {
		uniqueEventDescriptor = eventDescriptor;
		return this;
	}

	public EventBusBuilder buildEventDispatchStrategy() {
		DispatchStrategy dispatchStrategy;
		if (asyncEventDescriptor != null) {
			dispatchStrategy = new AsyncDispatchStrategy(asyncEventDescriptor);
		} else {
			dispatchStrategy = new SyncDispatchStrategy();
		}
		if (uniqueEventDescriptor != null) {
			dispatchStrategy = new UniqueDispatchStrategy(
					uniqueEventDescriptor, dispatchStrategy);
		}
		eventBusBuilder.eventDispatchStrategy(dispatchStrategy);
		return eventBusBuilder;
	}
}
