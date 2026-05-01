package com.shabic.livestock.application.command;

import java.util.UUID;

public record MoveAnimalCommand(UUID animalId, UUID toLocationId) {
}
