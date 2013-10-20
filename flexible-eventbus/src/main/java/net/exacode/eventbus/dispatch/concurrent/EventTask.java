package net.exacode.eventbus.dispatch.concurrent;

import net.exacode.eventbus.handler.MethodHandler;

public class EventTask implements Runnable {

	public static int threadNumberByLoadFactor(double loadFactor) {
		int cpus = Runtime.getRuntime().availableProcessors();
		int maxThreads = (int) (cpus * loadFactor);
		return (maxThreads > 0 ? maxThreads : 1);
	}

	private final Object event;

	private final MethodHandler handler;

	public EventTask(Object event, MethodHandler handler) {
		this.event = event;
		this.handler = handler;
	}

	@Override
	public void run() {
		handler.handleEvent(event);
	}

}