package com.shabic.livestock.domain.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface OutboxRepository {
	void enqueue(UUID id, String topic, String messageKey, String payload, Instant createdAt);

	List<OutboxMessage> findUnpublished(int limit);

	void markPublished(UUID id, Instant publishedAt);

	record OutboxMessage(UUID id, String topic, String messageKey, String payload) {}
}
