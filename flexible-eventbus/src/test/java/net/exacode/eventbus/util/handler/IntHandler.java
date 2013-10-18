package net.exacode.eventbus.util.handler;

import java.util.ArrayList;
import java.util.List;

import net.exacode.eventbus.EventHandler;

import org.fest.assertions.api.Assertions;

/**
 * A simple EventHadnler mock that records Integers.
 * 
 * For testing fun, also includes a landmine method that EventBus tests are
 * required <em>not</em> to call ({@link #methodWithoutAnnotation(Integer)}).
 * 
 * @author Cliff Biffle
 * @author mendlik
 */
public class IntHandler implements TestEventHandler<Integer> {
	private final List<Integer> events = new ArrayList<Integer>();

	@EventHandler
	public void hereHaveAnInteger(int integer) {
		events.add(integer);
	}

	public void methodWithoutAnnotation(String string) {
		Assertions
				.fail("Event bus must not call methods without appropriate annotation!");
	}

	@Override
	public List<Integer> getEvents() {
		return events;
	}
}
