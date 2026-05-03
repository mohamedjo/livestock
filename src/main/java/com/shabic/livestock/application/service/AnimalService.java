package com.shabic.livestock.application.service;

import com.shabic.livestock.application.command.MoveAnimalCommand;
import com.shabic.livestock.application.command.RegisterAnimalCommand;
import com.shabic.livestock.application.command.SellAnimalCommand;
import com.shabic.livestock.domain.events.AnimalCreated;
import com.shabic.livestock.domain.events.AnimalMoved;
import com.shabic.livestock.domain.events.AnimalSold;
import com.shabic.livestock.domain.model.AnimalHistoryRecord;
import com.shabic.livestock.domain.model.aggregate.Animal;
import com.shabic.livestock.domain.model.valueobject.AnimalStatus;
import com.shabic.livestock.domain.model.valueobject.Gender;
import com.shabic.livestock.domain.model.valueobject.MethodAcquired;
import com.shabic.livestock.domain.repository.AnimalHistoryRepository;
import com.shabic.livestock.domain.repository.AnimalRepository;
import com.shabic.livestock.infrastructure.messaging.AnimalEventPublisher;
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
	private final AnimalHistoryAppenderService animalHistoryAppender;
	private final AnimalEventPublisher publisher;

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

		AnimalCreated animalCreatedEvent = new AnimalCreated(
				registeredAnimal.getId(),
				registeredAnimal.getFarmId(),
				registeredAnimal.getType(),
				registeredAt
		);
		animalHistoryAppender.append(registeredAnimal.getId(), animalCreatedEvent);
		publisher.publish(animalCreatedEvent);

		return registeredAnimal.getId();
	}

	@Transactional
	public void move(MoveAnimalCommand moveAnimalCommand) {
		Animal animal = animalRepo.findById(moveAnimalCommand.animalId())
				.orElseThrow(() -> new IllegalArgumentException("animal not found"));
		UUID previousLocationId = animal.getCurrentLocationId();

		animal.moveTo(moveAnimalCommand.toLocationId());
		animalRepo.save(animal);

		Instant movedAt = Instant.now();
		AnimalMoved animalMovedEvent = new AnimalMoved(
				animal.getId(),
				animal.getFarmId(),
				previousLocationId,
				animal.getCurrentLocationId(),
				movedAt
		);
		animalHistoryAppender.append(animal.getId(), animalMovedEvent);
		publisher.publish(animalMovedEvent);
	}

	@Transactional
	public void sell(SellAnimalCommand sellAnimalCommand) {
		Animal animal = animalRepo.findById(sellAnimalCommand.animalId())
				.orElseThrow(() -> new IllegalArgumentException("animal not found"));
		animal.sell();
		animalRepo.save(animal);

		Instant soldAt = Instant.now();
		AnimalSold animalSoldEvent = new AnimalSold(animal.getId(), animal.getFarmId(), soldAt);
		animalHistoryAppender.append(animal.getId(), animalSoldEvent);
		publisher.publish(animalSoldEvent);
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
