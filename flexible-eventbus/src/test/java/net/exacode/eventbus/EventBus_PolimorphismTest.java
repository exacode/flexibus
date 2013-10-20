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
import net.exacode.eventbus.util.handler.ComparableHandler;
import net.exacode.eventbus.util.handler.ObjectHandler;
import net.exacode.eventbus.util.handler.StringHandler;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class EventBus_PolimorphismTest {

	private EventBus bus;

	@Before
	public void setUp() {
		bus = new EventBus();
	}

	@Test
	public void shouldDeliverStringEventToObjectHandler() {
		// given
		ObjectHandler handler = new ObjectHandler();
		bus.register(handler);

		// when
		bus.post(EventBusTestUtils.STRING_EVENT);

		// then
		EventBusTestUtils.checkEventHandling(handler,
				EventBusTestUtils.STRING_EVENT);
	}

	@Test
	public void shouldDeliverEventToBothHandlingMethodsInTypeRelatedOrder() {
		// given
		final List<Object> events = new ArrayList<Object>();
		final int[] handlingOrder = new int[] { 0, 0 };
		Object handler = new Object() {

			int order = 1;

			@EventHandler
			public void handleStringEvent(String event) {
				events.add(event);
				handlingOrder[0] = order++;
			}

			@EventHandler
			public void handleObjectEvent(Object event) {
				events.add(event);
				handlingOrder[1] = order++;
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
		Assertions.assertThat(handlingOrder).isEqualTo(new int[] { 1, 2 });
	}

	@Test
	public void shouldDeliverEventToBothHandlingMethodsInTypeRelatedOrder_reverse() {
		// given
		final List<Object> events = new ArrayList<Object>();
		final int[] handlingOrder = new int[] { 0, 0 };
		Object handler = new Object() {

			int order = 1;

			@EventHandler
			public void handleObjectEvent(Object event) {
				events.add(event);
				handlingOrder[1] = order++;
			}

			@EventHandler
			public void handleStringEvent(String event) {
				events.add(event);
				handlingOrder[0] = order++;
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
		Assertions.assertThat(handlingOrder).isEqualTo(new int[] { 1, 2 });
	}

	@Test
	public void shouldDeliverStringEventToComparableHandler() {
		// given
		ComparableHandler<String> handler = new ComparableHandler<String>();
		bus.register(handler);

		// when
		bus.post(EventBusTestUtils.STRING_EVENT);

		// then
		EventBusTestUtils.checkEventHandling(handler,
				EventBusTestUtils.STRING_EVENT);
	}

	/**
	 * Tests that events are distributed to any subscribers to their type or any
	 * supertype, including interfaces and superclasses.
	 * 
	 * Also checks delivery ordering in such cases.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void shouldDeliverEventsByTheirSupertypes() {
		// given
		ObjectHandler objectHandler = new ObjectHandler();
		ComparableHandler comparableHandler = new ComparableHandler<Object>();
		StringHandler stringHandler = new StringHandler();
		bus.register(stringHandler);
		bus.register(objectHandler);
		bus.register(comparableHandler);

		// when
		bus.post(EventBusTestUtils.STRING_EVENT);
		bus.post(EventBusTestUtils.OBJECT_EVENT);
		bus.post(EventBusTestUtils.INTEGER_EVENT);

		// then
		EventBusTestUtils.checkEventHandling(stringHandler,
				EventBusTestUtils.STRING_EVENT);
		EventBusTestUtils.checkEventHandling(comparableHandler, Arrays
				.asList(EventBusTestUtils.STRING_EVENT,
						EventBusTestUtils.INTEGER_EVENT));
		EventBusTestUtils.checkEventHandling(objectHandler, Arrays
				.asList(EventBusTestUtils.STRING_EVENT,
						EventBusTestUtils.INTEGER_EVENT,
						EventBusTestUtils.OBJECT_EVENT));
	}

}
