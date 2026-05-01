package com.shabic.livestock.domain.model;

import java.time.Instant;
import java.util.UUID;

public record AnimalHistoryRecord(
		UUID id,
		UUID animalId,
		String eventType,
		String eventData,
		Instant createdAt
) {
}

