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

	public static void registerHandlers(EventBus eventBus, List<?> handlers) {
		for (Object handler : handlers) {
			eventBus.register(handler);
		}
	}

	public static <E> void checkEventHandling(TestEventHandler<E> handler,
			List<? extends E> events) {
		List<E> handledEvents = handler.getEvents();
		Assertions.assertThat(handledEvents.size()).isEqualTo(events.size());
		for (E event : events) {
			Assertions.assertThat(handledEvents.contains(event)).isTrue();
		}
	}

	public static <E> void checkEventHandling(TestEventHandler<E> handler,
			E event) {
		List<E> handledEvents = handler.getEvents();
		Assertions.assertThat(handledEvents.size()).isEqualTo(1);
		Assertions.assertThat(handledEvents.contains(event)).isTrue();
	}

	public static <E> void checkEventHandling(
			List<? extends TestEventHandler<E>> handlers,
			List<? extends E> events) {
		for (TestEventHandler<E> handler : handlers) {
			checkEventHandling(handler, events);
		}
	}

	public static <E> void checkEventHandling(TestEventHandler<E> handler,
			E event, int times) {
		List<E> handledEvents = handler.getEvents();
		Assertions.assertThat(handledEvents.size()).isEqualTo(times);
		Assertions.assertThat(handledEvents.contains(event)).isTrue();
	}

	public static <E> void checkEventHandling(
			List<? extends TestEventHandler<E>> handlers, E event, int times) {
		for (TestEventHandler<E> handler : handlers) {
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
