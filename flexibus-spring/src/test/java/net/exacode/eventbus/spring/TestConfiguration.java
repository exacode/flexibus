package net.exacode.eventbus.spring;

import net.exacode.eventbus.EventBus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = TestConfiguration.class)
public class TestConfiguration {

	@Bean
	public EventBus eventBus() {
		return EventBus.builder().withSyncDispatchStrategy().buildEventBus();
	}

	@Bean
	public SpringEventBusPostProcessor springEventBusPostProcessor() {
		return new SpringEventBusPostProcessor(eventBus());
	}

}
