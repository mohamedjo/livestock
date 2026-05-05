package com.shabic.livestock.application.service;

import com.shabic.livestock.application.command.RegisterAnimalCommand;
import com.shabic.livestock.application.command.UpdateAnimalCommand;
import com.shabic.livestock.domain.model.AnimalHistoryRecord;
import com.shabic.livestock.domain.model.aggregate.Animal;
import com.shabic.livestock.domain.model.valueobject.AnimalStatus;
import com.shabic.livestock.domain.model.valueobject.Gender;
import com.shabic.livestock.domain.model.valueobject.MethodAcquired;
import com.shabic.livestock.domain.repository.AnimalHistoryRepository;
import com.shabic.livestock.domain.repository.AnimalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnimalService {
	private final AnimalRepository animalRepo;
	private final AnimalHistoryRepository historyRepo;

	@Transactional
	public UUID register(RegisterAnimalCommand registerAnimalCommand) {
		Instant registeredAt = Instant.now();
		UUID newAnimalId = UUID.randomUUID();

		String normalizedTagNumber = normalizeOptionalString(registerAnimalCommand.tagNumber());
		if (normalizedTagNumber != null && animalRepo.findByTagNumber(normalizedTagNumber).isPresent()) {
			throw new IllegalArgumentException("tagNumber already exists");
		}
		if (registerAnimalCommand.motherAnimalId() != null) {
			animalRepo.findById(registerAnimalCommand.motherAnimalId())
					.orElseThrow(() -> new IllegalArgumentException("mother animal not found"));
		}
		Gender gender = Gender.fromNullableString(registerAnimalCommand.gender());
		MethodAcquired methodAcquired = MethodAcquired.fromNullableString(registerAnimalCommand.methodAcquired());
		Set<String> feedTypes = registerAnimalCommand.feedTypes() == null || registerAnimalCommand.feedTypes().isEmpty()
				? Set.of()
				: Set.copyOf(registerAnimalCommand.feedTypes());
		AnimalStatus initialStatus = AnimalStatus.parseInitialStatus(registerAnimalCommand.status());

		Animal registeredAnimal = Animal.register(
				newAnimalId,
				normalizedTagNumber,
				registerAnimalCommand.type(),
				registerAnimalCommand.breed(),
				gender,
				registerAnimalCommand.birthDate(),
				registerAnimalCommand.farmId(),
				registerAnimalCommand.initialLocationId(),
				registeredAt,
				registerAnimalCommand.motherAnimalId(),
				registerAnimalCommand.shedId(),
				registerAnimalCommand.batchId(),
				registerAnimalCommand.assignDate(),
				methodAcquired,
				feedTypes,
				registerAnimalCommand.labelsKeywords(),
				registerAnimalCommand.internalId(),
				registerAnimalCommand.coloring(),
				registerAnimalCommand.additionalTagNumbers(),
				registerAnimalCommand.electronicId(),
				registerAnimalCommand.markingLeft(),
				registerAnimalCommand.markingRight(),
				registerAnimalCommand.description(),
				initialStatus
		);

		animalRepo.save(registeredAnimal);

		return registeredAnimal.getId();
	}

	@Transactional
	public Animal update(UpdateAnimalCommand updateAnimalCommand) {
		Animal existingAnimal = animalRepo.findById(updateAnimalCommand.animalId())
				.orElseThrow(() -> new IllegalArgumentException("animal not found"));
		RegisterAnimalCommand details = updateAnimalCommand.details();
		if (!existingAnimal.getFarmId().equals(details.farmId())) {
			throw new IllegalArgumentException("farmId cannot be changed");
		}

		String normalizedTagNumber = normalizeOptionalString(details.tagNumber());
		if (normalizedTagNumber != null) {
			animalRepo.findByTagNumber(normalizedTagNumber)
					.filter(candidate -> !candidate.getId().equals(updateAnimalCommand.animalId()))
					.ifPresent(candidate -> {
						throw new IllegalArgumentException("tagNumber already exists");
					});
		}
		if (details.motherAnimalId() != null) {
			if (details.motherAnimalId().equals(updateAnimalCommand.animalId())) {
				throw new IllegalArgumentException("motherAnimalId cannot reference the same animal");
			}
			animalRepo.findById(details.motherAnimalId())
					.orElseThrow(() -> new IllegalArgumentException("mother animal not found"));
		}

		Gender gender = Gender.fromNullableString(details.gender());
		MethodAcquired methodAcquired = MethodAcquired.fromNullableString(details.methodAcquired());
		Set<String> feedTypes = details.feedTypes() == null || details.feedTypes().isEmpty()
				? Set.of()
				: Set.copyOf(details.feedTypes());
		AnimalStatus updatedStatus = AnimalStatus.parseInitialStatus(details.status());

		Animal updatedAnimal = Animal.rehydrate(
				existingAnimal.getId(),
				normalizedTagNumber,
				details.type(),
				details.breed(),
				gender,
				details.birthDate(),
				details.farmId(),
				details.motherAnimalId(),
				details.shedId(),
				details.batchId(),
				details.assignDate(),
				methodAcquired,
				feedTypes,
				details.labelsKeywords(),
				details.internalId(),
				details.coloring(),
				details.additionalTagNumbers(),
				details.electronicId(),
				details.markingLeft(),
				details.markingRight(),
				details.description(),
				details.initialLocationId(),
				updatedStatus,
				existingAnimal.getCreatedAt()
		);

		animalRepo.save(updatedAnimal);
		return updatedAnimal;
	}

	@Transactional(readOnly = true)
	public List<Animal> getByFarm(UUID farmId) {
		return animalRepo.findByFarmId(farmId);
	}

	@Transactional(readOnly = true)
	public Animal getDetails(UUID animalId) {
		return animalRepo.findById(animalId)
				.orElseThrow(() -> new IllegalArgumentException("animal not found"));
	}

	@Transactional(readOnly = true)
	public List<AnimalHistoryRecord> history(UUID animalId) {
		return historyRepo.findByAnimalId(animalId);
	}

	private static String normalizeOptionalString(String raw) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		return raw.trim();
	}
}
