/*
 * Copyright (C) 2007 The Guava Authors
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
import net.exacode.eventbus.util.handler.IntHandler;
import net.exacode.eventbus.util.handler.ObjectHandler;
import net.exacode.eventbus.util.handler.StringHandler;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link EventBus} with default Configuration.
 * 
 * @author mendlik
 */
public class EventBus_BasicTest {

	private EventBus bus;

	@Before
	public void setUp() {
		bus = EventBus.builder().withSyncDispatchStrategy().buildEventBus();
	}

	@Test
	public void shouldDistributeSingleEventToSingleHandler() {
		// given
		StringHandler handler = new StringHandler();
		bus.register(handler);

		// when
		bus.post(EventBusTestUtils.STRING_EVENT);

		// then
		EventBusTestUtils.checkEventHandling(handler,
				Arrays.asList(EventBusTestUtils.STRING_EVENT));
	}

	@Test
	public void shouldDistributeSingleEventToAnonymousHandler() {
		// given
		final List<String> events = new ArrayList<String>();
		Object handler = new Object() {

			@EventHandler
			public void handleEvent(String event) {
				events.add(event);
			}

		};
		bus.register(handler);

		// when
		bus.post(EventBusTestUtils.STRING_EVENT);

		// then
		Assertions.assertThat(events.size()).isEqualTo(1);
		Assertions.assertThat(events.get(0)).isEqualTo(
				EventBusTestUtils.STRING_EVENT);
	}

	@Test
	public void shouldDeliverEventToToBothHandlingMethods() {
		// given
		final List<String> events = new ArrayList<String>();
		Object handler = new Object() {

			@EventHandler
			public void handleEvent(String event) {
				events.add(event);
			}

			@EventHandler
			public void handleEventTwo(String event) {
				events.add(event);
			}

		};
		bus.register(handler);

		// when
		bus.post(EventBusTestUtils.STRING_EVENT);

		// then
		Assertions.assertThat(events.size()).isEqualTo(2);
		Assertions.assertThat(events.get(0)).isEqualTo(
				EventBusTestUtils.STRING_EVENT);
		Assertions.assertThat(events.get(1)).isEqualTo(
				EventBusTestUtils.STRING_EVENT);
	}

	@Test
	public void shouldDistributeEventsInPostingOrder() {
		// given
		ObjectHandler handler = new ObjectHandler();
		Object[] events = new Object[] { new Object(), new Object() };
		bus.register(handler);

		// when
		for (int i = 0; i < events.length; ++i) {
			bus.post(events[i]);
		}

		// then
		EventBusTestUtils.checkEventHandling(handler, Arrays.asList(events));
	}

	@Test
	public void shouldDistributeEventsToDynamicallyRegisteredHandlers() {
		// given
		StringHandler handler1 = new StringHandler();
		StringHandler handler2 = new StringHandler();

		// when
		bus.register(handler1);
		bus.post(EventBusTestUtils.STRING_EVENT);
		bus.register(handler2);
		bus.post(EventBusTestUtils.STRING_EVENT);

		// then
		EventBusTestUtils.checkEventHandling(handler1,
				EventBusTestUtils.STRING_EVENT, 2);
		EventBusTestUtils.checkEventHandling(handler2,
				EventBusTestUtils.STRING_EVENT);
	}

	@Test
	public void shouldDistributePrimitiveEventToIntHandler() {
		// given
		IntHandler handler = new IntHandler();
		bus.register(handler);

		// when
		bus.post(EventBusTestUtils.INTEGER_EVENT);

		// then
		EventBusTestUtils.checkEventHandling(handler,
				Arrays.asList(EventBusTestUtils.INTEGER_EVENT));
	}

	@Test
	public void shouldDistributeSingleEventToHandlers() {
		// given
		List<StringHandler> handlers = Arrays.asList(new StringHandler(),
				new StringHandler());
		EventBusTestUtils.registerHandlers(bus, handlers);

		// when
		bus.post(EventBusTestUtils.STRING_EVENT);

		// then
		EventBusTestUtils.checkEventHandling(handlers,
				Arrays.asList(EventBusTestUtils.STRING_EVENT));
	}

	@Test
	public void shouldDistributeMultipleEventsToHandlers() {
		// given
		List<StringHandler> handlers = Arrays.asList(new StringHandler(),
				new StringHandler());
		EventBusTestUtils.registerHandlers(bus, handlers);
		int eventCount = 3;

		// when
		EventBusTestUtils.post(bus, EventBusTestUtils.STRING_EVENT, 3);

		// then
		EventBusTestUtils.checkEventHandling(handlers,
				EventBusTestUtils.STRING_EVENT, eventCount);
	}

	@Test
	public void shouldDistributeDifferentEventsToDifferentHandlers() {
		// given
		StringHandler stringHandler = new StringHandler();
		IntHandler integerHandler = new IntHandler();
		EventBusTestUtils.registerHandlers(bus,
				Arrays.asList(stringHandler, integerHandler));

		// when
		EventBusTestUtils.post(bus, Arrays
				.asList(EventBusTestUtils.STRING_EVENT,
						EventBusTestUtils.INTEGER_EVENT));

		// then
		EventBusTestUtils.checkEventHandling(stringHandler,
				EventBusTestUtils.STRING_EVENT);
		EventBusTestUtils.checkEventHandling(integerHandler,
				EventBusTestUtils.INTEGER_EVENT);
	}

	@Test
	public void shouldDoNothingOnNoHandlers() {
		// when
		bus.post(new Object());
	}

	@Test
	public void shouldNotDeliverNullEvent() {
		// given
		StringHandler handler = new StringHandler();
		bus.register(handler);
		String event = null;

		// when
		bus.post(event);

		// then
		Assertions.assertThat(handler.getEvents().size()).isEqualTo(0);
	}

	public void shouldNotThrowExceptionOnUnregisterNotRegisteredHandlers() {
		// when
		bus.unregister(new Object());
		bus.unregister(new StringHandler());
	}

	@Test
	public void shouldUnregisterHandler() {
		// given
		StringHandler handler = new StringHandler();

		// when
		bus.register(handler);
		bus.post(EventBusTestUtils.STRING_EVENT);
		bus.unregister(handler);
		bus.post(EventBusTestUtils.STRING_EVENT);

		// then
		EventBusTestUtils.checkEventHandling(handler,
				EventBusTestUtils.STRING_EVENT);
	}

	@Test
	public void shouldDistributeEventsToRegisteredHandlersOnly() {
		// given
		StringHandler handler1 = new StringHandler();
		StringHandler handler2 = new StringHandler();

		// when
		bus.register(handler1);
		bus.post(EventBusTestUtils.STRING_EVENT);
		bus.register(handler2);
		bus.post(EventBusTestUtils.STRING_EVENT);
		bus.unregister(handler1);
		bus.post(EventBusTestUtils.STRING_EVENT);
		bus.unregister(handler2);
		bus.post(EventBusTestUtils.STRING_EVENT);

		// then
		EventBusTestUtils.checkEventHandling(handler1,
				EventBusTestUtils.STRING_EVENT, 2);
		EventBusTestUtils.checkEventHandling(handler2,
				EventBusTestUtils.STRING_EVENT, 2);

	}

}