package com.shabic.livestock.domain.service;

import com.shabic.livestock.domain.model.aggregate.Animal;
import com.shabic.livestock.domain.model.valueobject.Gender;
import com.shabic.livestock.domain.model.valueobject.TagNumber;
import com.shabic.livestock.domain.repository.AnimalRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class AnimalRegistrationService {
	private final AnimalRepository repository;

	public AnimalRegistrationService(AnimalRepository repository) {
		this.repository = repository;
	}

	public Animal register(
			UUID id,
			TagNumber tagNumber,
			String type,
			String breed,
			Gender gender,
			LocalDate birthDate,
			UUID farmId,
			UUID initialLocationId,
			Instant now
	) {
		if (repository.findByTagNumber(tagNumber).isPresent()) {
			throw new IllegalArgumentException("tagNumber already exists");
		}
		return Animal.register(id, tagNumber, type, breed, gender, birthDate, farmId, initialLocationId, now);
	}
}

