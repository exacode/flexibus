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
package net.exacode.eventbus.util;

import java.util.List;

import net.exacode.eventbus.DeadEvent;
import net.exacode.eventbus.EventBus;
import net.exacode.eventbus.util.handler.DeadEventHandler;
import net.exacode.eventbus.util.handler.TestEventHandler;

import org.fest.assertions.api.Assertions;

public class EventBusTestUtils {

	public static final String STRING_EVENT = "Hello";

	public static final int INTEGER_EVENT = 256;

	public static final Object OBJECT_EVENT = new Object();

	public static void registerHandlers(EventBus eventBus, List<?> handlers) {
		for (Object handler : handlers) {
			eventBus.register(handler);
		}
	}

	public static void checkEventHandling(TestEventHandler<?> handler,
			List<?> events) {
		List<?> handledEvents = handler.getEvents();
		Assertions.assertThat(handledEvents.size()).isEqualTo(events.size());
		for (Object event : events) {
			Assertions.assertThat(handledEvents.contains(event)).isTrue();
		}
	}

	public static void checkEventHandling(TestEventHandler<?> handler,
			Object event) {
		List<?> handledEvents = handler.getEvents();
		Assertions.assertThat(handledEvents.size()).isEqualTo(1);
		Assertions.assertThat(handledEvents.contains(event)).isTrue();
	}

	public static void checkEventHandling(
			List<? extends TestEventHandler<?>> handlers, List<?> events) {
		for (TestEventHandler<?> handler : handlers) {
			checkEventHandling(handler, events);
		}
	}

	public static void checkEventHandling(TestEventHandler<?> handler,
			Object event, int times) {
		List<?> handledEvents = handler.getEvents();
		Assertions.assertThat(handledEvents.size()).isEqualTo(times);
		Assertions.assertThat(handledEvents.contains(event)).isTrue();
	}

	public static void checkEventHandling(
			List<? extends TestEventHandler<?>> handlers, Object event,
			int times) {
		for (TestEventHandler<?> handler : handlers) {
			checkEventHandling(handler, event, times);
		}
	}

	public static void checkDeadEventHandling(
			TestEventHandler<DeadEvent> handler, Object event) {
		List<DeadEvent> handledEvents = handler.getEvents();
		Assertions.assertThat(handledEvents.size()).isEqualTo(1);
		Assertions.assertThat(handledEvents.get(0).getEvent()).isEqualTo(event);
	}

	public static void checkDeadEventHandling(List<DeadEventHandler> handlers,
			Object event) {
		for (TestEventHandler<DeadEvent> handler : handlers) {
			checkDeadEventHandling(handler, event);
		}
	}

	public static <E> void checkDeadEventHandling(
			TestEventHandler<DeadEvent> handler, E event, int times) {
		List<DeadEvent> handledEvents = handler.getEvents();
		Assertions.assertThat(handledEvents.size()).isEqualTo(times);
		for (DeadEvent deadEvent : handledEvents) {
			Assertions.assertThat(deadEvent.getEvent()).isEqualTo(event);
		}
	}

	public static <E> void checkDeadEventHandling(
			List<? extends TestEventHandler<DeadEvent>> handlers, E event,
			int times) {
		for (TestEventHandler<DeadEvent> handler : handlers) {
			checkDeadEventHandling(handler, event, times);
		}
	}

	public static void post(EventBus eventBus, Object event, int times) {
		for (int i = 0; i < times; ++i) {
			eventBus.post(event);
		}
	}

	public static void post(EventBus eventBus, List<?> events) {
		for (Object event : events) {
			eventBus.post(event);
		}
	}
}
