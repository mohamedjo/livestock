package com.shabic.livestock.config;

import com.shabic.livestock.config.correlation.CorrelationId;
import com.shabic.livestock.config.correlation.CorrelationIdContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class FarmServiceClientConfig {

	@Bean
	public RestClient.Builder restClientBuilder() {
		return RestClient.builder()
				.requestInterceptor((request, body, execution) -> {
					CorrelationIdContext.get().ifPresent(id ->
							request.getHeaders().set(CorrelationId.HEADER_NAME, id));
					return execution.execute(request, body);
				});
	}
}
