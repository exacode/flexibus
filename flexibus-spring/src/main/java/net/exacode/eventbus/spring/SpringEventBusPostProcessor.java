package net.exacode.eventbus.spring;

import net.exacode.eventbus.EventBus;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class SpringEventBusPostProcessor implements BeanPostProcessor {

	private final EventBus eventBus;

	public SpringEventBusPostProcessor(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		eventBus.register(bean);
		return bean;
	}

}
