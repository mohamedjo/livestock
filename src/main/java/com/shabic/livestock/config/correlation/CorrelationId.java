package com.shabic.livestock.config.correlation;

import java.util.function.Function;

public final class CorrelationId {
	public static final String HEADER_NAME = "X-Correlation-Id";
	public static final String MDC_KEY = "correlationId";

	private static final String[] INCOMING_HEADER_NAMES = {
			"X-Correlation-Id",
			"X-Correlation-ID",
			"Correlation-Id",
			"Correlation-ID",
			"X-Request-Id"
	};

	private CorrelationId() {
	}

	public static String resolveFromHeaders(Function<String, String> headerLookup) {
		for (String headerName : INCOMING_HEADER_NAMES) {
			String value = headerLookup.apply(headerName);
			if (value != null && !value.isBlank()) {
				return value.trim();
			}
		}
		return null;
	}
}
