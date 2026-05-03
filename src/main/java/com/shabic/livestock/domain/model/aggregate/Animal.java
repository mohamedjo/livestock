package com.shabic.livestock.domain.model.aggregate;

import com.shabic.livestock.domain.model.valueobject.AnimalStatus;
import com.shabic.livestock.domain.model.valueobject.Gender;
import com.shabic.livestock.domain.model.valueobject.MethodAcquired;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Animal {
	@EqualsAndHashCode.Include
	private final UUID id;
	private final String tagNumber;
	private final String type;
	private final String breed;
	private final Gender gender;
	private final LocalDate birthDate;
	private final UUID farmId;

	private final UUID motherAnimalId;
	private final UUID shedId;
	private final UUID batchId;
	private final LocalDate assignDate;
	private final MethodAcquired methodAcquired;
	private final Set<String> feedTypes;
	private final String labelsKeywords;
	private final String internalId;
	private final String coloring;
	private final String additionalTagNumbers;
	private final String electronicId;
	private final String markingLeft;
	private final String markingRight;
	private final String description;

	private UUID currentLocationId;
	private AnimalStatus status;
	private final Instant createdAt;

	private static String blankToNull(String s) {
		if (s == null || s.isBlank()) return null;
		return s.trim();
	}

	private static Animal create(
			UUID id,
			String tagNumber,
			String type,
			String breed,
			Gender gender,
			LocalDate birthDate,
			UUID farmId,
			UUID motherAnimalId,
			UUID shedId,
			UUID batchId,
			LocalDate assignDate,
			MethodAcquired methodAcquired,
			Set<String> feedTypes,
			String labelsKeywords,
			String internalId,
			String coloring,
			String additionalTagNumbers,
			String electronicId,
			String markingLeft,
			String markingRight,
			String description,
			UUID currentLocationId,
			AnimalStatus status,
			Instant createdAt
	) {
		Objects.requireNonNull(id, "id");
		if (type == null || type.isBlank()) throw new IllegalArgumentException("type is required");
		Objects.requireNonNull(farmId, "farmId");
		Objects.requireNonNull(status, "status");
		Objects.requireNonNull(createdAt, "createdAt");
		String normalizedType = type.trim();
		String normalizedBreed = (breed == null || breed.isBlank()) ? null : breed.trim();
		Gender g = gender == null ? Gender.UNKNOWN : gender;
		Set<String> feeds = feedTypes == null ? Set.of() : Set.copyOf(feedTypes);
		return new Animal(
				id,
				blankToNull(tagNumber),
				normalizedType,
				normalizedBreed,
				g,
				birthDate,
				farmId,
				motherAnimalId,
				shedId,
				batchId,
				assignDate,
				methodAcquired,
				feeds,
				blankToNull(labelsKeywords),
				blankToNull(internalId),
				blankToNull(coloring),
				blankToNull(additionalTagNumbers),
				blankToNull(electronicId),
				blankToNull(markingLeft),
				blankToNull(markingRight),
				blankToNull(description),
				currentLocationId,
				status,
				createdAt
		);
	}

	public static Animal register(
			UUID id,
			String tagNumber,
			String type,
			String breed,
			Gender gender,
			LocalDate birthDate,
			UUID farmId,
			UUID initialLocationId,
			Instant now,
			UUID motherAnimalId,
			UUID shedId,
			UUID batchId,
			LocalDate assignDate,
			MethodAcquired methodAcquired,
			Set<String> feedTypes,
			String labelsKeywords,
			String internalId,
			String coloring,
			String additionalTagNumbers,
			String electronicId,
			String markingLeft,
			String markingRight,
			String description,
			AnimalStatus initialStatus
	) {
		AnimalStatus initial = initialStatus != null ? initialStatus : AnimalStatus.ALIVE;
		return create(
				id,
				tagNumber,
				type,
				breed,
				gender,
				birthDate,
				farmId,
				motherAnimalId,
				shedId,
				batchId,
				assignDate,
				methodAcquired,
				feedTypes,
				labelsKeywords,
				internalId,
				coloring,
				additionalTagNumbers,
				electronicId,
				markingLeft,
				markingRight,
				description,
				initialLocationId,
				initial,
				now
		);
	}

	public static Animal rehydrate(
			UUID id,
			String tagNumber,
			String type,
			String breed,
			Gender gender,
			LocalDate birthDate,
			UUID farmId,
			UUID motherAnimalId,
			UUID shedId,
			UUID batchId,
			LocalDate assignDate,
			MethodAcquired methodAcquired,
			Set<String> feedTypes,
			String labelsKeywords,
			String internalId,
			String coloring,
			String additionalTagNumbers,
			String electronicId,
			String markingLeft,
			String markingRight,
			String description,
			UUID currentLocationId,
			AnimalStatus status,
			Instant createdAt
	) {
		return create(
				id,
				tagNumber,
				type,
				breed,
				gender,
				birthDate,
				farmId,
				motherAnimalId,
				shedId,
				batchId,
				assignDate,
				methodAcquired,
				feedTypes,
				labelsKeywords,
				internalId,
				coloring,
				additionalTagNumbers,
				electronicId,
				markingLeft,
				markingRight,
				description,
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

	public Animal snapshot() {
		return new Animal(
				id, tagNumber, type, breed, gender, birthDate, farmId,
				motherAnimalId, shedId, batchId, assignDate, methodAcquired, feedTypes,
				labelsKeywords, internalId, coloring, additionalTagNumbers, electronicId,
				markingLeft, markingRight, description,
				currentLocationId, status, createdAt
		);
	}
}
