package com.shabic.livestock.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalHistoryResponse {
	private UUID id;
	private UUID animalId;
	private String eventType;
	private String eventData;
	private Instant createdAt;
}
