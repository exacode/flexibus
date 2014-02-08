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
package net.exacode.eventbus.util.handler;

import java.util.ArrayList;
import java.util.List;

import net.exacode.eventbus.EventHandler;

import org.fest.assertions.api.Assertions;

/**
 * A simple EventHadnler mock that records Integers.
 * 
 * For testing fun, also includes a landmine method that EventBus tests are
 * required <em>not</em> to call ({@link #methodWithoutAnnotation(Integer)}).
 * 
 * @author Cliff Biffle
 * @author mendlik
 */
public class IntHandler implements TestEventHandler<Integer> {
	private final List<Integer> events = new ArrayList<Integer>();

	@EventHandler
	public void hereHaveAnInteger(int integer) {
		events.add(integer);
	}

	public void methodWithoutAnnotation(String string) {
		Assertions
				.fail("Event bus must not call methods without appropriate annotation!");
	}

	@Override
	public List<Integer> getEvents() {
		return events;
	}
}
