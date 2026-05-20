package com.shabic.livestock.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shabic.livestock.domain.repository.DeletedFarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FarmDeletedConsumer {
	private final ObjectMapper objectMapper;
	private final DeletedFarmRepository deletedFarmRepo;

	@KafkaListener(topics = "${livestock.kafka.topics.farm-deleted}")
	public void onFarmDeleted(String payload) {
		try {
			JsonNode json = objectMapper.readTree(payload);
			JsonNode farmIdNode = json.get("farmId");
			if (farmIdNode == null || farmIdNode.isNull()) {
				return;
			}
			UUID farmId = UUID.fromString(farmIdNode.asText());
			Instant deletedAt = parseTimestamp(json.get("timestamp"));
			deletedFarmRepo.markDeleted(farmId, deletedAt);
		} catch (Exception e) {
			throw new RuntimeException("Failed to consume FarmDeleted event", e);
		}
	}

	private static Instant parseTimestamp(JsonNode node) {
		if (node == null || node.isNull()) {
			return Instant.now();
		}
		return Instant.parse(node.asText());
	}
}
