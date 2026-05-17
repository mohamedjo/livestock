package com.shabic.livestock.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shabic.livestock.application.messaging.AnimalEventPublisher;
import com.shabic.livestock.domain.events.AnimalCreated;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaAnimalEventPublisher implements AnimalEventPublisher {
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	@Value("${livestock.kafka.topics.animal-created}")
	private String animalCreatedTopic;

	@Override
	public void publishAnimalCreated(AnimalCreated event) {
		try {
			String payload = objectMapper.writeValueAsString(event);
			kafkaTemplate.send(animalCreatedTopic, event.animalId().toString(), payload);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Failed to serialize AnimalCreated event", e);
		}
	}
}
