package com.shabic.livestock.application.command;

import java.time.LocalDate;
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
		UUID initialLocationId
) {
}
