package com.shabic.livestock.infrastructure.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

@Component
public class FarmServiceClient {
	private final RestClient restClient;
	private final String serviceToken;

	public FarmServiceClient(
			RestClient.Builder restClientBuilder,
			@Value("${livestock.farm-service.base-url}") String baseUrl,
			@Value("${livestock.farm-service.service-token:}") String serviceToken) {
		this.restClient = restClientBuilder.baseUrl(baseUrl).build();
		this.serviceToken = serviceToken == null ? "" : serviceToken.trim();
	}

	@CircuitBreaker(name = "farmService", fallbackMethod = "assertFarmExistsFallback")
	@Retry(name = "farmService")
	public void assertFarmExists(UUID farmId) {
		try {
			restClient.get()
					.uri("/api/farms/{id}", farmId)
					.headers(headers -> resolveAuthorization().ifPresent(headers::setBearerAuth))
					.retrieve()
					.toBodilessEntity();
		} catch (RestClientResponseException e) {
			if (isFarmNotFound(e)) {
				throw new IllegalArgumentException("farm not found");
			}
			if (e.getStatusCode().is4xxClientError()) {
				throw new IllegalArgumentException("farm service rejected request: " + e.getStatusCode());
			}
			throw new IllegalStateException("farm service unavailable: " + e.getStatusCode(), e);
		} catch (RestClientException e) {
			throw new IllegalStateException("farm service unavailable", e);
		}
	}

	@SuppressWarnings("unused")
	private void assertFarmExistsFallback(UUID farmId, Throwable cause) {
		throw new IllegalStateException("farm service unavailable", cause);
	}

	private static boolean isFarmNotFound(RestClientResponseException e) {
		if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
			return true;
		}
		String body = e.getResponseBodyAsString();
		return body != null && body.contains("farm not found");
	}

	private java.util.Optional<String> resolveAuthorization() {
		var auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof JwtAuthenticationToken jwtAuth) {
			return java.util.Optional.of(jwtAuth.getToken().getTokenValue());
		}
		if (!serviceToken.isEmpty()) {
			return java.util.Optional.of(serviceToken);
		}
		return java.util.Optional.empty();
	}
}
