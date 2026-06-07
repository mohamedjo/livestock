package com.shabic.livestock.config.correlation;

import org.springframework.boot.kafka.autoconfigure.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class CorrelationIdKafkaConfig {

	@Bean
	CorrelationIdRecordInterceptor correlationIdRecordInterceptor() {
		return new CorrelationIdRecordInterceptor();
	}

	@Bean
	ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
			ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
			ConsumerFactory<Object, Object> kafkaConsumerFactory,
			CorrelationIdRecordInterceptor correlationIdRecordInterceptor) {
		ConcurrentKafkaListenerContainerFactory<Object, Object> factory =
				new ConcurrentKafkaListenerContainerFactory<>();
		configurer.configure(factory, kafkaConsumerFactory);
		factory.setRecordInterceptor(correlationIdRecordInterceptor);
		return factory;
	}
}
