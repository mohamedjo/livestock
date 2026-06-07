package com.shabic.livestock.config.correlation;

import org.slf4j.MDC;

import java.util.Optional;
import java.util.UUID;

public final class CorrelationIdContext {
	private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

	private CorrelationIdContext() {
	}

	public static void set(String correlationId) {
		if (correlationId == null || correlationId.isBlank()) {
			return;
		}
		String value = correlationId.trim();
		CURRENT.set(value);
		MDC.put(CorrelationId.MDC_KEY, value);
	}

	public static Optional<String> get() {
		return Optional.ofNullable(CURRENT.get());
	}

	public static String getOrGenerate() {
		return get().orElseGet(() -> {
			String generated = UUID.randomUUID().toString();
			set(generated);
			return generated;
		});
	}

	public static void clear() {
		CURRENT.remove();
		MDC.remove(CorrelationId.MDC_KEY);
	}
}
