package com.shabic.livestock.application.command;

import java.util.UUID;

/**
 * Full replacement update for an existing animal (same field set as registration, plus target id).
 */
public record UpdateAnimalCommand(UUID animalId, RegisterAnimalCommand details) {
}
