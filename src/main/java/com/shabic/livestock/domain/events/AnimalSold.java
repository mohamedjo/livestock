package com.shabic.livestock.domain.events;

import java.time.Instant;
import java.util.UUID;

public record AnimalSold(
	UUID animalId,
	UUID farmId,
	Instant timestamp
) implements DomainEvent {
	@Override
	public String eventType() {
		return "AnimalSold";
	}
}
