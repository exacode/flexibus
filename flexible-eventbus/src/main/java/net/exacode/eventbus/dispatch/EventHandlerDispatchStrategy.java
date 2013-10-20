package net.exacode.eventbus.dispatch;

import java.util.Arrays;
import java.util.Collection;

import net.exacode.eventbus.EventHandler;
import net.exacode.eventbus.dispatch.concurrent.AsyncDispatchStrategy;
import net.exacode.eventbus.dispatch.concurrent.SyncDispatchStrategy;
import net.exacode.eventbus.handler.MethodHandler;

/**
 * Dynamic {@link DispatchStrategy} designed for {@link EventHandler} annotated
 * handlers.
 * 
 * @author mendlik
 * 
 */
public class EventHandlerDispatchStrategy implements DispatchStrategy {

	private final DispatchStrategy asyncDispatchStrategy;

	private final DispatchStrategy syncDispatchStrategy;

	public EventHandlerDispatchStrategy() {
		asyncDispatchStrategy = new AsyncDispatchStrategy();
		syncDispatchStrategy = new SyncDispatchStrategy();
	}

	public EventHandlerDispatchStrategy(DispatchStrategy asyncDispatchStrategy,
			DispatchStrategy syncDispatchStrategy) {
		this.asyncDispatchStrategy = asyncDispatchStrategy;
		this.syncDispatchStrategy = syncDispatchStrategy;
	}

	@Override
	public void dispatchEvent(Object event,
			Collection<MethodHandler> handlerMethods) {
		for (MethodHandler methodHandler : handlerMethods) {
			DispatchStrategy dispatchStrategy = buildDispatchStrategy(methodHandler);
			dispatchStrategy.dispatchEvent(event, Arrays.asList(methodHandler));
		}
	}

	private DispatchStrategy buildDispatchStrategy(MethodHandler methodHandler) {
		EventHandler handlerAnnotation = methodHandler.getMethod()
				.getAnnotation(EventHandler.class);
		if (handlerAnnotation != null && handlerAnnotation.async()) {
			return asyncDispatchStrategy;
		}
		return syncDispatchStrategy;
	}

}
