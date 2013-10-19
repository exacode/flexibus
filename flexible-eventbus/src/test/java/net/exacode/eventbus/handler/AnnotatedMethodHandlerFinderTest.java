package net.exacode.eventbus.handler;

import java.util.Map;
import java.util.Set;

import net.exacode.eventbus.EventHandler;
import net.exacode.eventbus.util.handler.StringHandler;

import org.fest.assertions.api.Assertions;
import org.junit.Test;

public class AnnotatedMethodHandlerFinderTest {
	private final AnnotatedMethodHandlerFinder<EventHandler> finder = new AnnotatedMethodHandlerFinder<EventHandler>(
			EventHandler.class);

	private static class MultipleHandler {

		@EventHandler
		public void handleString(String event) {

		}

		@EventHandler
		public void handleObject(Object event) {

		}

	}

	@Test
	public void shouldNotFindHandlers() throws NoSuchMethodException,
			SecurityException {
		// given
		Object handler = new Object();

		// when
		Map<Class<?>, Set<MethodHandler>> map = finder
				.findHandlerMethods(handler);

		// then
		Assertions.assertThat(map.size()).isEqualTo(0);
	}

	@Test
	public void shouldFindHandlerMethod() throws NoSuchMethodException,
			SecurityException {
		// given
		StringHandler handler = new StringHandler();

		// when
		Map<Class<?>, Set<MethodHandler>> map = finder
				.findHandlerMethods(handler);

		// then
		Assertions.assertThat(map.size()).isEqualTo(1);
		Set<MethodHandler> methodHandlers = map.get(String.class);
		Assertions.assertThat(methodHandlers.size()).isEqualTo(1);
		MethodHandler methodHandler = methodHandlers.iterator().next();
		Assertions.assertThat(methodHandler.getTarget()).isEqualTo(handler);
		Assertions.assertThat(methodHandler.getMethod()).isEqualTo(
				handler.getClass().getMethod("hereHaveAString", String.class));
	}

	@Test
	public void shouldFindMultipleHandlerMethods()
			throws NoSuchMethodException, SecurityException {
		// given
		MultipleHandler handler = new MultipleHandler();

		// when
		Map<Class<?>, Set<MethodHandler>> map = finder
				.findHandlerMethods(handler);

		// then
		Assertions.assertThat(map.size()).isEqualTo(2);
		Set<MethodHandler> methodHandlers = map.get(String.class);
		Assertions.assertThat(methodHandlers.size()).isEqualTo(1);
		MethodHandler methodHandler = methodHandlers.iterator().next();
		Assertions.assertThat(methodHandler.getTarget()).isEqualTo(handler);
		Assertions.assertThat(methodHandler.getMethod()).isEqualTo(
				handler.getClass().getMethod("handleString", String.class));
		methodHandlers = map.get(Object.class);
		Assertions.assertThat(methodHandlers.size()).isEqualTo(1);
		methodHandler = methodHandlers.iterator().next();
		Assertions.assertThat(methodHandler.getTarget()).isEqualTo(handler);
		Assertions.assertThat(methodHandler.getMethod()).isEqualTo(
				handler.getClass().getMethod("handleObject", Object.class));
	}

	@Test
	public void shouldFindHandlerMethodInAnonymousClass()
			throws NoSuchMethodException, SecurityException {
		// given
		Object handler = new Object() {
			@EventHandler
			public void handle(String event) {

			}
		};

		// when
		Map<Class<?>, Set<MethodHandler>> map = finder
				.findHandlerMethods(handler);

		// then
		Assertions.assertThat(map.size()).isEqualTo(1);
		Set<MethodHandler> methodHandlers = map.get(String.class);
		Assertions.assertThat(methodHandlers.size()).isEqualTo(1);
		MethodHandler methodHandler = methodHandlers.iterator().next();
		Assertions.assertThat(methodHandler.getTarget()).isEqualTo(handler);
		Assertions.assertThat(methodHandler.getMethod()).isEqualTo(
				handler.getClass().getMethod("handle", String.class));
	}

}
