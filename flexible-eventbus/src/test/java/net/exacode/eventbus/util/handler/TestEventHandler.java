package net.exacode.eventbus.util.handler;

import java.util.List;

public interface TestEventHandler<E> {
	public List<E> getEvents();

}
