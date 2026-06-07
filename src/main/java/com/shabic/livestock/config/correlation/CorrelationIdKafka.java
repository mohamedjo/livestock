package com.shabic.livestock.config.correlation;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public final class CorrelationIdKafka {
	private CorrelationIdKafka() {
	}

	public static org.apache.kafka.clients.producer.ProducerRecord<String, String> producerRecord(
			String topic,
			String key,
			String payload,
			String correlationId) {
		var record = new org.apache.kafka.clients.producer.ProducerRecord<>(topic, key, payload);
		if (correlationId != null && !correlationId.isBlank()) {
			record.headers().add(
					CorrelationId.HEADER_NAME,
					correlationId.trim().getBytes(StandardCharsets.UTF_8));
		}
		return record;
	}

	public static Optional<String> fromHeaders(Headers headers) {
		if (headers == null) {
			return Optional.empty();
		}
		Header header = headers.lastHeader(CorrelationId.HEADER_NAME);
		if (header == null || header.value() == null) {
			return Optional.empty();
		}
		return Optional.of(new String(header.value(), StandardCharsets.UTF_8));
	}

	public static Optional<String> fromRecord(ConsumerRecord<?, ?> record) {
		return fromHeaders(record.headers());
	}
}
