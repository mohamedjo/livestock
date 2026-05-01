package com.shabic.livestock.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "animal")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalEntity {
	@Id
	@Column(name = "id", nullable = false)
	private UUID id;

	@Column(name = "tag_number", nullable = false, unique = true, length = 64)
	private String tagNumber;

	@Column(name = "type", nullable = false, length = 64)
	private String type;

	@Column(name = "breed", length = 128)
	private String breed;

	@Enumerated(EnumType.STRING)
	@Column(name = "gender", length = 32)
	private com.shabic.livestock.domain.model.valueobject.Gender gender;

	@Column(name = "birth_date")
	private LocalDate birthDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 32)
	private com.shabic.livestock.domain.model.valueobject.AnimalStatus status;

	@Column(name = "farm_id", nullable = false)
	private UUID farmId;

	@Column(name = "current_location_id")
	private UUID currentLocationId;

	@Column(name = "created_at", nullable = false)
	private Instant createdAt;
}
