package com.shabic.livestock.application.handlers;

import com.shabic.livestock.application.commands.RegisterAnimalCommand;
import com.shabic.livestock.domain.events.AnimalCreated;
import com.shabic.livestock.domain.model.Animal;
import com.shabic.livestock.domain.model.Gender;
import com.shabic.livestock.domain.model.TagNumber;
import com.shabic.livestock.infrastructure.messaging.AnimalEventPublisher;
import com.shabic.livestock.infrastructure.persistence.AnimalPersistenceMapper;
import com.shabic.livestock.infrastructure.persistence.repositories.AnimalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterAnimalHandler {
	private final AnimalJpaRepository animalRepo;
	private final AnimalHistoryAppender historyAppender;
	private final AnimalEventPublisher publisher;
	private final AnimalPersistenceMapper mapper;

	@Transactional
	public UUID handle(RegisterAnimalCommand cmd) {
		if (animalRepo.findByTagNumber(cmd.getTagNumber()).isPresent()) {
			throw new IllegalArgumentException("tagNumber already exists");
		}

		Instant now = Instant.now();
		UUID id = UUID.randomUUID();

		Gender gender = parseGender(cmd.getGender());
		Animal animal = Animal.register(
				id,
				new TagNumber(cmd.getTagNumber()),
				cmd.getType(),
				cmd.getBreed(),
				gender,
				cmd.getBirthDate(),
				cmd.getFarmId(),
				cmd.getInitialLocationId(),
				now
		);

		animalRepo.save(mapper.toEntity(animal));

		var created = new AnimalCreated(animal.id(), animal.farmId(), animal.type(), now);
		historyAppender.append(animal.id(), created);
		publisher.publish(created);

		return animal.id();
	}

	private static Gender parseGender(String raw) {
		if (raw == null || raw.isBlank()) return Gender.UNKNOWN;
		try {
			return Gender.valueOf(raw.trim().toUpperCase(Locale.ROOT));
		} catch (Exception e) {
			return Gender.UNKNOWN;
		}
	}
}
