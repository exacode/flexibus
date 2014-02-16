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
package net.exacode.eventbus.dispatch.concurrent;

import java.util.Collection;
import java.util.concurrent.Executor;

import net.exacode.eventbus.dispatch.DispatchStrategy;
import net.exacode.eventbus.handler.MethodHandler;

/**
 * Responsible for asynchronous event dispatching.
 * <p>
 * All events are executed in separate threads. You can customize thread pool by
 * providing your own {@link Executor}.
 * 
 * @author mendlik
 * 
 */
public class AsyncDispatchStrategy implements DispatchStrategy {

	private final Executor executor;

	public AsyncDispatchStrategy(Executor executor) {
		this.executor = executor;
	}

	@Override
	public void dispatchEvent(final Object event,
			Collection<MethodHandler> handlerMethods) {
		for (final MethodHandler methodHandler : handlerMethods) {
			Runnable eventTask = new EventTask(event, methodHandler);
			executor.execute(eventTask);
		}
	}

}
