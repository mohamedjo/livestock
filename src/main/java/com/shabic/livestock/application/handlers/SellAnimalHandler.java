package com.shabic.livestock.application.handlers;

import com.shabic.livestock.application.commands.SellAnimalCommand;
import com.shabic.livestock.domain.events.AnimalSold;
import com.shabic.livestock.domain.model.Animal;
import com.shabic.livestock.infrastructure.messaging.AnimalEventPublisher;
import com.shabic.livestock.infrastructure.persistence.AnimalPersistenceMapper;
import com.shabic.livestock.infrastructure.persistence.repositories.AnimalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class SellAnimalHandler {
	private final AnimalJpaRepository animalRepo;
	private final AnimalHistoryAppender historyAppender;
	private final AnimalEventPublisher publisher;
	private final AnimalPersistenceMapper mapper;

	@Transactional
	public void handle(SellAnimalCommand cmd) {
		var entity = animalRepo.findById(cmd.getAnimalId())
				.orElseThrow(() -> new IllegalArgumentException("animal not found"));

		Animal animal = mapper.toDomain(entity);
		animal.sell();
		animalRepo.save(mapper.toEntity(animal));

		Instant now = Instant.now();
		var sold = new AnimalSold(animal.id(), animal.farmId(), now);
		historyAppender.append(animal.id(), sold);
		publisher.publish(sold);
	}
}
