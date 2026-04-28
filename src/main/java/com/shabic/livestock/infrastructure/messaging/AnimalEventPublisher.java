package com.shabic.livestock.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shabic.livestock.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnimalEventPublisher {
	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	@Value("${livestock.kafka.topics.animal-created}")
	private String animalCreatedTopic;
	@Value("${livestock.kafka.topics.animal-moved}")
	private String animalMovedTopic;
	@Value("${livestock.kafka.topics.animal-sold}")
	private String animalSoldTopic;

	public void publish(DomainEvent event) {
		String topic = switch (event.eventType()) {
			case "AnimalCreated" -> animalCreatedTopic;
			case "AnimalMoved" -> animalMovedTopic;
			case "AnimalSold" -> animalSoldTopic;
			default -> throw new IllegalArgumentException("Unsupported event type: " + event.eventType());
		};
		try {
			String payload = objectMapper.writeValueAsString(event);
			kafkaTemplate.send(topic, payload);
		} catch (Exception e) {
			throw new RuntimeException("Failed to publish event " + event.eventType(), e);
		}
	}
}
