package com.shabic.livestock.application.service;

import com.shabic.livestock.api.dto.MoveAnimalRequest;
import com.shabic.livestock.api.dto.RegisterAnimalRequest;
import com.shabic.livestock.application.handlers.AnimalHistoryAppender;
import com.shabic.livestock.domain.events.AnimalCreated;
import com.shabic.livestock.domain.events.AnimalMoved;
import com.shabic.livestock.domain.events.AnimalSold;
import com.shabic.livestock.domain.model.AnimalHistoryRecord;
import com.shabic.livestock.domain.model.aggregate.Animal;
import com.shabic.livestock.domain.model.valueobject.Gender;
import com.shabic.livestock.domain.model.valueobject.TagNumber;
import com.shabic.livestock.domain.repository.AnimalHistoryRepository;
import com.shabic.livestock.domain.repository.AnimalRepository;
import com.shabic.livestock.domain.service.AnimalRegistrationService;
import com.shabic.livestock.infrastructure.messaging.AnimalEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnimalService {
	private final AnimalRepository animalRepo;
	private final AnimalHistoryRepository historyRepo;
	private final AnimalHistoryAppender historyAppender;
	private final AnimalEventPublisher publisher;

	@Transactional
	public UUID register(RegisterAnimalRequest req) {
		Instant now = Instant.now();
		UUID id = UUID.randomUUID();

		var registration = new AnimalRegistrationService(animalRepo);
		Gender gender = Gender.fromNullableString(req.getGender());
		Animal animal = registration.register(
				id,
				new TagNumber(req.getTagNumber()),
				req.getType(),
				req.getBreed(),
				gender,
				req.getBirthDate(),
				req.getFarmId(),
				req.getInitialLocationId(),
				now
		);

		animalRepo.save(animal);

		var created = new AnimalCreated(animal.id(), animal.farmId(), animal.type(), now);
		historyAppender.append(animal.id(), created);
		publisher.publish(created);

		return animal.id();
	}

	@Transactional
	public void move(UUID animalId, MoveAnimalRequest req) {
		Animal animal = animalRepo.findById(animalId)
				.orElseThrow(() -> new IllegalArgumentException("animal not found"));
		UUID from = animal.currentLocationId();

		animal.moveTo(req.getToLocationId());
		animalRepo.save(animal);

		Instant now = Instant.now();
		var moved = new AnimalMoved(animal.id(), animal.farmId(), from, animal.currentLocationId(), now);
		historyAppender.append(animal.id(), moved);
		publisher.publish(moved);
	}

	@Transactional
	public void sell(UUID animalId) {
		Animal animal = animalRepo.findById(animalId)
				.orElseThrow(() -> new IllegalArgumentException("animal not found"));
		animal.sell();
		animalRepo.save(animal);

		Instant now = Instant.now();
		var sold = new AnimalSold(animal.id(), animal.farmId(), now);
		historyAppender.append(animal.id(), sold);
		publisher.publish(sold);
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
}

