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

import java.util.Arrays;
import java.util.List;

import net.exacode.eventbus.util.EventBusTestUtils;
import net.exacode.eventbus.util.handler.IntHandler;
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
		bus = new EventBus();
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

}