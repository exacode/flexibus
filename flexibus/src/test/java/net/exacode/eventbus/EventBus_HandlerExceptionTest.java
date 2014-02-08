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
import java.util.List;

import net.exacode.eventbus.exception.ExceptionHandler;
import net.exacode.eventbus.util.EventBusTestUtils;
import net.exacode.eventbus.util.handler.StringHandler;
import net.exacode.eventbus.util.handler.TestEventHandler;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;

public class EventBus_HandlerExceptionTest {
	private static class RecordingExceptionHandler implements ExceptionHandler {
		private final List<Throwable> exceptions = new ArrayList<Throwable>();

		@Override
		public void handle(Throwable e) {
			exceptions.add(e);
		}

		public List<Throwable> getExceptions() {
			return exceptions;
		}

	};

	private static class ThrowableObjectHandler implements
			TestEventHandler<Object> {

		private final Throwable exception = new RuntimeException();

		private final List<Object> events = new ArrayList<Object>();

		@EventHandler
		public void handle(Object event) throws Throwable {
			events.add(event);
			throw exception;
		}

		@Override
		public List<Object> getEvents() {
			return events;
		}

		public Throwable getException() {
			return exception;
		}

	}

	private EventBus bus;

	private RecordingExceptionHandler exceptionHandler;

	@Before
	public void setUp() {
		exceptionHandler = new RecordingExceptionHandler();
		bus = EventBus.builder().exceptionHandler(exceptionHandler)
				.buildEventBus();
	}

	@Test
	public void shouldPassExceptionToExceptionHandler() {
		// given
		ThrowableObjectHandler throwableObjectHandler = new ThrowableObjectHandler();
		bus.register(throwableObjectHandler);

		// when
		bus.post(EventBusTestUtils.STRING_EVENT);

		// then
		Assertions.assertThat(exceptionHandler.getExceptions().size())
				.isEqualTo(1);
		Assertions.assertThat(exceptionHandler.getExceptions().get(0))
				.isEqualTo(throwableObjectHandler.getException());
	}

	@Test
	public void shouldNotPassAnyException() {
		// given
		StringHandler handler = new StringHandler();
		bus.register(handler);

		// when
		bus.post(EventBusTestUtils.STRING_EVENT);

		// then
		Assertions.assertThat(exceptionHandler.getExceptions().size())
				.isEqualTo(0);
	}

	@Test
	public void shouldDistributeAllEventsDespiteOfHandlerException() {
		// given
		StringHandler stringHandler1 = new StringHandler();
		StringHandler stringHandler2 = new StringHandler();
		ThrowableObjectHandler throwableObjectHandler = new ThrowableObjectHandler();
		bus.register(stringHandler1);
		bus.register(throwableObjectHandler);
		bus.register(stringHandler2);

		// when
		bus.post(EventBusTestUtils.STRING_EVENT);

		// then
		EventBusTestUtils.checkEventHandling(stringHandler1,
				EventBusTestUtils.STRING_EVENT);
		EventBusTestUtils.checkEventHandling(throwableObjectHandler,
				EventBusTestUtils.STRING_EVENT);
		EventBusTestUtils.checkEventHandling(stringHandler2,
				EventBusTestUtils.STRING_EVENT);
	}
}
