package com.shabic.livestock.infrastructure.persistence.adapters;

import com.shabic.livestock.domain.repository.OutboxRepository;
import com.shabic.livestock.infrastructure.persistence.entities.OutboxEventEntity;
import com.shabic.livestock.infrastructure.persistence.repositories.OutboxEventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaOutboxRepository implements OutboxRepository {
	private final OutboxEventJpaRepository jpa;

	@Override
	public void enqueue(UUID id, String topic, String messageKey, String payload, Instant createdAt) {
		jpa.save(OutboxEventEntity.builder()
				.id(id)
				.topic(topic)
				.messageKey(messageKey)
				.payload(payload)
				.createdAt(createdAt)
				.build());
	}

	@Override
	public List<OutboxMessage> findUnpublished(int limit) {
		return jpa.findUnpublished(PageRequest.of(0, limit)).stream()
				.map(entity -> new OutboxMessage(
						entity.getId(),
						entity.getTopic(),
						entity.getMessageKey(),
						entity.getPayload()))
				.toList();
	}

	@Override
	public void markPublished(UUID id, Instant publishedAt) {
		OutboxEventEntity entity = jpa.findById(id)
				.orElseThrow(() -> new IllegalStateException("outbox event not found: " + id));
		entity.setPublishedAt(publishedAt);
		jpa.save(entity);
	}
}
