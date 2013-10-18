package net.exacode.eventbus.util.handler;

import java.util.ArrayList;
import java.util.List;

import net.exacode.eventbus.DeadEvent;
import net.exacode.eventbus.EventHandler;

import org.fest.assertions.api.Assertions;

public class DeadEventHandler implements TestEventHandler<DeadEvent> {

	private final List<DeadEvent> events = new ArrayList<DeadEvent>();

	@EventHandler
	public void hereHaveADeadEvent(DeadEvent deadEvent) {
		events.add(deadEvent);
	}

	public void methodWithoutAnnotation(String string) {
		Assertions
				.fail("Event bus must not call methods without appropriate annotation!");
	}

	@Override
	public List<DeadEvent> getEvents() {
		return events;
	}

}
