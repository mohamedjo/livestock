package com.shabic.livestock.config;

import com.shabic.livestock.config.correlation.CorrelationId;
import com.shabic.livestock.config.correlation.CorrelationIdContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import java.time.Duration;

@Configuration
public class FarmServiceClientConfig {

	@Bean
	RestClientCustomizer correlationIdRestClientCustomizer() {
		return builder -> builder.requestInterceptor((request, body, execution) -> {
			CorrelationIdContext.get().ifPresent(id ->
					request.getHeaders().set(CorrelationId.HEADER_NAME, id));
			return execution.execute(request, body);
		});
	}

	@Bean
	RestClientCustomizer farmServiceTimeoutRestClientCustomizer(
			@Value("${livestock.farm-service.connect-timeout:2s}") Duration connectTimeout,
			@Value("${livestock.farm-service.read-timeout:3s}") Duration readTimeout) {
		return builder -> {
			SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
			requestFactory.setConnectTimeout(connectTimeout);
			requestFactory.setReadTimeout(readTimeout);
			builder.requestFactory(requestFactory);
		};
	}
}
