package com.shabic.livestock.domain.events;

import java.time.Instant;
import java.util.UUID;

public record AnimalCreated(
	UUID animalId,
	UUID farmId,
	String type,
	Instant timestamp
) implements DomainEvent {
	@Override
	public String eventType() {
		return "AnimalCreated";
	}
}
