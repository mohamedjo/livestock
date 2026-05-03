package com.shabic.livestock.application.command;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Application-layer input for registering an animal (not an HTTP DTO).
 */
public record RegisterAnimalCommand(
		String tagNumber,
		String type,
		String breed,
		String gender,
		LocalDate birthDate,
		UUID farmId,
		UUID initialLocationId,
		UUID motherAnimalId,
		UUID shedId,
		UUID batchId,
		LocalDate assignDate,
		String methodAcquired,
		List<String> feedTypes,
		String labelsKeywords,
		String internalId,
		String coloring,
		String additionalTagNumbers,
		String electronicId,
		String markingLeft,
		String markingRight,
		String description,
		String status
) {
}
