package com.shabic.livestock.application.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shabic.livestock.domain.events.DomainEvent;
import com.shabic.livestock.infrastructure.persistence.entities.AnimalHistoryEntity;
import com.shabic.livestock.infrastructure.persistence.repositories.AnimalHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AnimalHistoryAppender {
	private final AnimalHistoryJpaRepository historyRepo;
	private final ObjectMapper objectMapper;

	public void append(UUID animalId, String eventType, Object payload, Instant now) {
		try {
			String json = objectMapper.writeValueAsString(payload);
			historyRepo.save(AnimalHistoryEntity.builder()
					.id(UUID.randomUUID())
					.animalId(animalId)
					.eventType(eventType)
					.eventData(json)
					.createdAt(now)
					.build());
		} catch (Exception e) {
			throw new RuntimeException("Failed to write animal_history event", e);
		}
	}

	public void append(UUID animalId, DomainEvent event) {
		append(animalId, event.eventType(), event, event.timestamp());
	}
}
