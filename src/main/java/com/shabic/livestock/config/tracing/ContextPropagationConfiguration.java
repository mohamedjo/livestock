package com.shabic.livestock.config.tracing;

import org.springframework.core.task.support.ContextPropagatingTaskDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ContextPropagationConfiguration {

	@Bean
	ContextPropagatingTaskDecorator contextPropagatingTaskDecorator() {
		return new ContextPropagatingTaskDecorator();
	}
}
