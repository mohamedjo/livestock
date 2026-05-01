package com.shabic.livestock.domain.model.aggregate;

import com.shabic.livestock.domain.model.valueobject.AnimalStatus;
import com.shabic.livestock.domain.model.valueobject.Gender;
import com.shabic.livestock.domain.model.valueobject.TagNumber;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public final class Animal {
	private final UUID id;
	private final TagNumber tagNumber;
	private final String type;
	private final String breed;
	private final Gender gender;
	private final LocalDate birthDate;
	private final UUID farmId;

	private UUID currentLocationId;
	private AnimalStatus status;
	private final Instant createdAt;

	private Animal(
			UUID id,
			TagNumber tagNumber,
			String type,
			String breed,
			Gender gender,
			LocalDate birthDate,
			UUID farmId,
			UUID currentLocationId,
			AnimalStatus status,
			Instant createdAt
	) {
		this.id = Objects.requireNonNull(id, "id");
		this.tagNumber = Objects.requireNonNull(tagNumber, "tagNumber");
		if (type == null || type.isBlank()) throw new IllegalArgumentException("type is required");
		this.type = type.trim();
		this.breed = (breed == null || breed.isBlank()) ? null : breed.trim();
		this.gender = gender == null ? Gender.UNKNOWN : gender;
		this.birthDate = birthDate;
		this.farmId = Objects.requireNonNull(farmId, "farmId");
		this.currentLocationId = currentLocationId;
		this.status = Objects.requireNonNull(status, "status");
		this.createdAt = Objects.requireNonNull(createdAt, "createdAt");
	}

	public static Animal register(
			UUID id,
			TagNumber tagNumber,
			String type,
			String breed,
			Gender gender,
			LocalDate birthDate,
			UUID farmId,
			UUID initialLocationId,
			Instant now
	) {
		return new Animal(
				id,
				tagNumber,
				type,
				breed,
				gender,
				birthDate,
				farmId,
				initialLocationId,
				AnimalStatus.ALIVE,
				now
		);
	}

	public static Animal rehydrate(
			UUID id,
			TagNumber tagNumber,
			String type,
			String breed,
			Gender gender,
			LocalDate birthDate,
			UUID farmId,
			UUID currentLocationId,
			AnimalStatus status,
			Instant createdAt
	) {
		return new Animal(
				id,
				tagNumber,
				type,
				breed,
				gender,
				birthDate,
				farmId,
				currentLocationId,
				status,
				createdAt
		);
	}

	public void moveTo(UUID newLocationId) {
		ensureActive();
		if (newLocationId == null) throw new IllegalArgumentException("newLocationId is required");
		if (newLocationId.equals(currentLocationId)) return;
		this.currentLocationId = newLocationId;
	}

	public void sell() {
		ensureActive();
		this.status = AnimalStatus.SOLD;
	}

	public void slaughter() {
		ensureActive();
		this.status = AnimalStatus.SLAUGHTERED;
	}

	public void markDead() {
		ensureActive();
		this.status = AnimalStatus.DEAD;
	}

	private void ensureActive() {
		if (status != AnimalStatus.ALIVE) {
			throw new IllegalStateException("Only ALIVE animals can be changed (current=" + status + ")");
		}
	}

	public UUID id() { return id; }
	public TagNumber tagNumber() { return tagNumber; }
	public String type() { return type; }
	public String breed() { return breed; }
	public Gender gender() { return gender; }
	public LocalDate birthDate() { return birthDate; }
	public AnimalStatus status() { return status; }
	public UUID farmId() { return farmId; }
	public UUID currentLocationId() { return currentLocationId; }
	public Instant createdAt() { return createdAt; }

	// JavaBean-style getters for tooling (MapStruct/Jackson/etc.)
	public UUID getId() { return id(); }
	public TagNumber getTagNumber() { return tagNumber(); }
	public String getType() { return type(); }
	public String getBreed() { return breed(); }
	public Gender getGender() { return gender(); }
	public LocalDate getBirthDate() { return birthDate(); }
	public AnimalStatus getStatus() { return status(); }
	public UUID getFarmId() { return farmId(); }
	public UUID getCurrentLocationId() { return currentLocationId(); }
	public Instant getCreatedAt() { return createdAt(); }

	public Animal snapshot() {
		return new Animal(
				id, tagNumber, type, breed, gender, birthDate, farmId, currentLocationId, status, createdAt
		);
	}
}

