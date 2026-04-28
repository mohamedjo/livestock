package com.shabic.livestock.application.handlers;

import com.shabic.livestock.application.commands.MoveAnimalCommand;
import com.shabic.livestock.domain.events.AnimalMoved;
import com.shabic.livestock.domain.model.Animal;
import com.shabic.livestock.infrastructure.messaging.AnimalEventPublisher;
import com.shabic.livestock.infrastructure.persistence.AnimalPersistenceMapper;
import com.shabic.livestock.infrastructure.persistence.repositories.AnimalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MoveAnimalHandler {
	private final AnimalJpaRepository animalRepo;
	private final AnimalHistoryAppender historyAppender;
	private final AnimalEventPublisher publisher;
	private final AnimalPersistenceMapper mapper;

	@Transactional
	public void handle(MoveAnimalCommand cmd) {
		var entity = animalRepo.findById(cmd.getAnimalId())
				.orElseThrow(() -> new IllegalArgumentException("animal not found"));

		Animal animal = mapper.toDomain(entity);
		UUID from = animal.currentLocationId();

		animal.moveTo(cmd.getToLocationId());

		animalRepo.save(mapper.toEntity(animal));

		Instant now = Instant.now();
		var moved = new AnimalMoved(animal.id(), animal.farmId(), from, animal.currentLocationId(), now);
		historyAppender.append(animal.id(), moved);
		publisher.publish(moved);
	}
}
