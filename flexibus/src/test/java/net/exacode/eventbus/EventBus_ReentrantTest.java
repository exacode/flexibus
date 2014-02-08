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
package net.exacode.eventbus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.exacode.eventbus.util.EventBusTestUtils;
import net.exacode.eventbus.util.handler.TestEventHandler;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class EventBus_ReentrantTest {
	private EventBus bus;

	@Before
	public void setUp() {
		bus = EventBus.builder().withSyncDispatchStrategy().buildEventBus();
	}

	@Test
	public void shouldDistributeSecondEventAfterDispatchingFirstEvent() {
		// given
		ReentrantEventHandler handler = new ReentrantEventHandler();
		bus.register(handler);

		// when
		bus.post(EventBusTestUtils.STRING_EVENT);

		// then
		Assertions
				.assertThat(handler.isSecondEventAfterFirstEventDispatching())
				.isTrue();
		EventBusTestUtils.checkEventHandling(handler, Arrays
				.asList(EventBusTestUtils.STRING_EVENT,
						EventBusTestUtils.INTEGER_EVENT));
	}

	public class ReentrantEventHandler implements TestEventHandler<Object> {
		private boolean ready = false;

		private boolean secondEventAfterFirstEventDispatching = false;

		private final List<Object> events = new ArrayList<Object>();

		@EventHandler
		public void listenForStrings(String event) {
			events.add(event);
			ready = false;
			try {
				bus.post(EventBusTestUtils.INTEGER_EVENT);
			} finally {
				ready = true;
			}
		}

		@EventHandler
		public void listenForDoubles(Integer event) {
			if (ready) {
				secondEventAfterFirstEventDispatching = true;
			}
			events.add(event);
		}

		@Override
		public List<Object> getEvents() {
			return events;
		}

		public boolean isSecondEventAfterFirstEventDispatching() {
			return secondEventAfterFirstEventDispatching;
		}

		public void setSecondEventAfterFirstEventDispatching(
				boolean secondEventAfterFirstEventDispatching) {
			this.secondEventAfterFirstEventDispatching = secondEventAfterFirstEventDispatching;
		}

	}
}
