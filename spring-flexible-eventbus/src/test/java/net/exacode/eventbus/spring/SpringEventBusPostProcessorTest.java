package net.exacode.eventbus.spring;

import java.util.List;

import net.exacode.eventbus.EventBus;
import net.exacode.eventbus.spring.util.StringHandler;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SpringEventBusPostProcessorTest extends SpringTestBase {

	private static final String STRING_EVENT = "hello";

	@Autowired
	private EventBus eventBus;

	@Autowired
	private StringHandler handler;

	@Before
	public void setup() {
		handler.getEvents().clear();
	}

	@Test
	public void shouldDeliverEvent() {
		// when
		eventBus.post(STRING_EVENT);

		// then
		List<String> events = handler.getEvents();
		Assertions.assertThat(events.size()).isEqualTo(1);
		Assertions.assertThat(events.get(0)).isEqualTo(STRING_EVENT);
	}

}
