package com.shabic.livestock.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "animal_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalHistoryEntity {
	@Id
	@Column(name = "id", nullable = false)
	private UUID id;

	@Column(name = "animal_id", nullable = false)
	private UUID animalId;

	@Column(name = "event_type", nullable = false, length = 128)
	private String eventType;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "event_data", nullable = false, columnDefinition = "jsonb")
	private String eventData;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
}
