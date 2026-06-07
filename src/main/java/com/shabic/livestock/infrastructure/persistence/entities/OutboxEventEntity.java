package com.shabic.livestock.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEventEntity {
	@Id
	@Column(name = "id", nullable = false)
	private UUID id;

	@Column(name = "topic", nullable = false, length = 255)
	private String topic;

	@Column(name = "message_key", nullable = false, length = 255)
	private String messageKey;

	@Column(name = "payload", nullable = false, columnDefinition = "TEXT")
	private String payload;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;

	@Column(name = "published_at")
	private Instant publishedAt;

	@Column(name = "correlation_id", length = 255)
	private String correlationId;
}
