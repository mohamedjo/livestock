package com.shabic.livestock.domain.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import java.time.Instant;
import java.util.UUID;

public record AnimalCreated(
		UUID eventId,
		UUID animalId,
		UUID farmId,
		String type,
		Instant timestamp
) implements DomainEvent {
	@JsonProperty(value = "eventType", access = Access.READ_ONLY)
	@Override
	public String eventType() {
		return "AnimalCreated";
	}
}
