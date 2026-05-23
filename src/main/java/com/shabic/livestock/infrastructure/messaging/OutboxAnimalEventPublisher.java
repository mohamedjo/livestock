package com.shabic.livestock.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shabic.livestock.application.messaging.AnimalEventPublisher;
import com.shabic.livestock.domain.events.AnimalCreated;
import com.shabic.livestock.domain.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxAnimalEventPublisher implements AnimalEventPublisher {
	private final OutboxRepository outboxRepo;
	private final ObjectMapper objectMapper;

	@Value("${livestock.kafka.topics.animal-created}")
	private String animalCreatedTopic;

	@Override
	public void publishAnimalCreated(AnimalCreated event) {
		try {
			String payload = objectMapper.writeValueAsString(event);
			outboxRepo.enqueue(
					event.eventId(),
					animalCreatedTopic,
					event.animalId().toString(),
					payload,
					event.timestamp()
			);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Failed to serialize AnimalCreated event", e);
		}
	}
}
