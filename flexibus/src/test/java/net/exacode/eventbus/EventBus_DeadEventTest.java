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
import net.exacode.eventbus.util.handler.DeadEventHandler;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;

/**
 * Test case for {@link EventBus} with default Configuration.
 * 
 * @author mendlik
 */
public class EventBus_DeadEventTest {

	private EventBus bus;

	@Before
	public void setUp() {
		bus = EventBus.builder().withSyncDispatchStrategy().buildEventBus();
	}

	@Test
	public void shouldDistributeDeadEventToSingleHandler() {
		// given
		DeadEventHandler handler = new DeadEventHandler();
		bus.register(handler);

		// when
		bus.post(EventBusTestUtils.STRING_EVENT);

		// then
		EventBusTestUtils.checkDeadEventHandling(handler,
				EventBusTestUtils.STRING_EVENT);
	}

	@Test
	public void shouldDistributeMultipleDeadEventsToSingleHandler() {
		// given
		DeadEventHandler handler = new DeadEventHandler();
		bus.register(handler);
		int times = 3;

		// when
		EventBusTestUtils.post(bus, EventBusTestUtils.STRING_EVENT, times);

		// then
		EventBusTestUtils.checkDeadEventHandling(handler,
				EventBusTestUtils.STRING_EVENT, times);
	}

	@Test
	public void shouldDistributeDeadEventToMultipleHandlers() {
		// given
		List<DeadEventHandler> handlers = Arrays.asList(new DeadEventHandler(),
				new DeadEventHandler());
		EventBusTestUtils.registerHandlers(bus, handlers);

		// when
		bus.post(EventBusTestUtils.STRING_EVENT);

		// then
		EventBusTestUtils.checkDeadEventHandling(handlers,
				EventBusTestUtils.STRING_EVENT);
	}

	@Test
	public void shouldDistributeMultipleDeadEventsToMultipleHandlers() {
		// given
		List<DeadEventHandler> handlers = Arrays.asList(new DeadEventHandler(),
				new DeadEventHandler());
		EventBusTestUtils.registerHandlers(bus, handlers);
		int times = 3;

		// when
		EventBusTestUtils.post(bus, EventBusTestUtils.STRING_EVENT, times);

		// then
		EventBusTestUtils.checkDeadEventHandling(handlers,
				EventBusTestUtils.STRING_EVENT, times);
	}

	@Test
	public void testDeliverExplicitDeadEvent() {
		// given
		DeadEventHandler handler = new DeadEventHandler();
		bus.register(handler);

		// when
		bus.post(new DeadEvent(EventBusTestUtils.STRING_EVENT));

		// then
		EventBusTestUtils.checkDeadEventHandling(handler,
				EventBusTestUtils.STRING_EVENT);
	}

	@Test
	public void shouldNotDeliverNullEvent() {
		// given
		DeadEventHandler handler = new DeadEventHandler();
		bus.register(handler);
		String event = null;

		// when
		bus.post(event);

		// then
		Assertions.assertThat(handler.getEvents().size()).isEqualTo(0);
	}

}