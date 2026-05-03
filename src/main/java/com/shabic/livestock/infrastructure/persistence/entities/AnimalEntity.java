package com.shabic.livestock.infrastructure.persistence.entities;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
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

	@Column(name = "tag_number", unique = true, length = 64)
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

	@Column(name = "mother_animal_id")
	private UUID motherAnimalId;

	@Column(name = "shed_id")
	private UUID shedId;

	@Column(name = "batch_id")
	private UUID batchId;

	@Column(name = "assign_date")
	private LocalDate assignDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "method_acquired", length = 64)
	private com.shabic.livestock.domain.model.valueobject.MethodAcquired methodAcquired;

	@ElementCollection(fetch = FetchType.LAZY)
	@CollectionTable(name = "animal_feed_type", joinColumns = @JoinColumn(name = "animal_id"))
	@Column(name = "feed_type", length = 128)
	@Builder.Default
	private Set<String> feedTypes = new HashSet<>();

	@Column(name = "labels_keywords", length = 512)
	private String labelsKeywords;

	@Column(name = "internal_id", length = 64)
	private String internalId;

	@Column(name = "coloring", length = 128)
	private String coloring;

	@Column(name = "additional_tag_numbers", length = 512)
	private String additionalTagNumbers;

	@Column(name = "electronic_id", length = 128)
	private String electronicId;

	@Column(name = "marking_left", length = 255)
	private String markingLeft;

	@Column(name = "marking_right", length = 255)
	private String markingRight;

	@Column(name = "description", columnDefinition = "TEXT")
	private String description;
}
