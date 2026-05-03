package com.shabic.livestock.domain.repository;

import com.shabic.livestock.domain.model.aggregate.Animal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnimalRepository {
	Optional<Animal> findById(UUID id);
	Optional<Animal> findByTagNumber(String tagNumber);
	List<Animal> findByFarmId(UUID farmId);
	void save(Animal animal);
}
