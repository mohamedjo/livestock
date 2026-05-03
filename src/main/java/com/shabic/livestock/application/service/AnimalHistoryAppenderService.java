package com.shabic.livestock.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shabic.livestock.domain.events.DomainEvent;
import com.shabic.livestock.domain.model.AnimalHistoryRecord;
import com.shabic.livestock.domain.repository.AnimalHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AnimalHistoryAppenderService {
	private final AnimalHistoryRepository historyRepo;
	private final ObjectMapper objectMapper;

	public void append(UUID animalId, String eventType, Object payload, Instant now) {
		try {
			String json = objectMapper.writeValueAsString(payload);
			historyRepo.save(new AnimalHistoryRecord(
					UUID.randomUUID(),
					animalId,
					eventType,
					json,
					now
			));
		} catch (Exception e) {
			throw new RuntimeException("Failed to write animal_history event", e);
		}
	}

	public void append(UUID animalId, DomainEvent event) {
		append(animalId, event.eventType(), event, event.timestamp());
	}
}
