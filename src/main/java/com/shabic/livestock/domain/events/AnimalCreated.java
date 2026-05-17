package com.shabic.livestock.domain.events;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public record AnimalCreated(
		UUID animalId,
		UUID farmId,
		String type,
		Instant timestamp
) implements DomainEvent {
	@JsonProperty("eventType")
	@Override
	public String eventType() {
		return "AnimalCreated";
	}
}
