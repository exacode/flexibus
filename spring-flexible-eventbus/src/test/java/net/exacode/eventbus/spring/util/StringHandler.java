package net.exacode.eventbus.spring.util;

import java.util.ArrayList;
import java.util.List;

import net.exacode.eventbus.EventHandler;

import org.springframework.stereotype.Component;

@Component
public class StringHandler {

	private final List<String> events = new ArrayList<String>();

	@EventHandler
	public void handle(String event) {
		events.add(event);
	}

	public List<String> getEvents() {
		return events;
	}

}
