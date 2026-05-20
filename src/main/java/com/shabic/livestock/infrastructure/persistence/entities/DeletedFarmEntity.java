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
@Table(name = "deleted_farm")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeletedFarmEntity {
	@Id
	@Column(name = "farm_id", nullable = false)
	private UUID farmId;

	@Column(name = "deleted_at", nullable = false)
	private Instant deletedAt;
}
