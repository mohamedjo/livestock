package com.shabic.livestock.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shabic.livestock.application.handlers.AnimalHistoryAppender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExternalAnimalEventsConsumer {
	private final ObjectMapper objectMapper;
	private final AnimalHistoryAppender historyAppender;

	@Value("${livestock.kafka.topics.animal-fed}")
	private String animalFedTopic;

	@Value("${livestock.kafka.topics.animal-vaccinated}")
	private String animalVaccinatedTopic;

	@KafkaListener(topics = "${livestock.kafka.topics.animal-fed}")
	public void onAnimalFed(String payload) {
		appendExternal("AnimalFed", payload);
	}

	@KafkaListener(topics = "${livestock.kafka.topics.animal-vaccinated}")
	public void onAnimalVaccinated(String payload) {
		appendExternal("AnimalVaccinated", payload);
	}

	private void appendExternal(String eventType, String payload) {
		try {
			JsonNode json = objectMapper.readTree(payload);
			JsonNode animalIdNode = json.get("animalId");
			if (animalIdNode == null || animalIdNode.isNull()) return;
			UUID animalId = UUID.fromString(animalIdNode.asText());
			historyAppender.append(animalId, eventType, json, Instant.now());
		} catch (Exception e) {
			// If payload is invalid we surface it (consumer retries depend on Kafka settings)
			throw new RuntimeException("Failed to consume external event " + eventType, e);
		}
	}
}

