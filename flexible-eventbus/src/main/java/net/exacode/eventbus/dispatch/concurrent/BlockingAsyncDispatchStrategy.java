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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.exacode.eventbus.dispatch.DispatchStrategy;
import net.exacode.eventbus.handler.MethodHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for asynchronous event dispatching.
 * <p>
 * All events are executed in separate threads. You can customize threads by
 * providing {@link ExecutorService}.
 * <p>
 * {@link #dispatchEvent(Object, Collection)} returns after all events are
 * dispatched and their threads stops.
 * 
 * @author mendlik
 * 
 */
public class BlockingAsyncDispatchStrategy implements DispatchStrategy {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ExecutorService executorService;

	public BlockingAsyncDispatchStrategy(ExecutorService executorService) {
		this.executorService = executorService;
	}

	public BlockingAsyncDispatchStrategy() {
		int threads = EventTask.threadNumberByLoadFactor(0.5);
		logger.debug("Creating thread pool of size: {}", threads);
		this.executorService = Executors.newFixedThreadPool(threads);
	}

	@Override
	public void dispatchEvent(final Object event,
			Collection<MethodHandler> handlerMethods) {
		List<Callable<Object>> tasks = new ArrayList<Callable<Object>>();
		for (final MethodHandler methodHandler : handlerMethods) {
			Runnable eventTask = new EventTask(event, methodHandler);
			tasks.add(Executors.callable(eventTask));
		}
		try {
			executorService.invokeAll(tasks);
		} catch (InterruptedException e) {
			logger.error("Could not dispatch events", e);
		}
	}
}
