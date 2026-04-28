package com.shabic.livestock.domain.events;

import java.time.Instant;
import java.util.UUID;

public record AnimalMoved(
	UUID animalId,
	UUID farmId,
	UUID fromLocationId,
	UUID toLocationId,
	Instant timestamp
) implements DomainEvent {
	@Override
	public String eventType() {
		return "AnimalMoved";
	}
}
