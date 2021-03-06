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

import net.exacode.eventbus.DeadEvent;
import net.exacode.eventbus.EventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeadEventLoggingHandler {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@EventHandler
	public void handle(DeadEvent deadEvent) {
		Object event = deadEvent.getEvent();
		logger.trace("Dead event of type: {}, object: {}", event.getClass(),
				event);
	}
}
