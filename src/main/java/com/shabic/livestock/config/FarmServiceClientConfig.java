package com.shabic.livestock.config;

import com.shabic.livestock.config.correlation.CorrelationId;
import com.shabic.livestock.config.correlation.CorrelationIdContext;
import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
