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

public class ComparableHandler<E> implements TestEventHandler<Comparable<E>> {
	private final List<Comparable<E>> events = new ArrayList<Comparable<E>>();

	@EventHandler
	public void hereHaveAComparableObj(Comparable<E> cmp) {
		events.add(cmp);
	}

	public void methodWithoutAnnotation(String string) {
		Assertions
				.fail("Event bus must not call methods without appropriate annotation!");
	}

	@Override
	public List<Comparable<E>> getEvents() {
		return events;
	}
}
