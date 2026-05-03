package com.shabic.livestock.infrastructure.persistence.adapters;

import com.shabic.livestock.domain.model.aggregate.Animal;
import com.shabic.livestock.domain.repository.AnimalRepository;
import com.shabic.livestock.infrastructure.persistence.AnimalPersistenceMapper;
import com.shabic.livestock.infrastructure.persistence.repositories.AnimalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaAnimalRepository implements AnimalRepository {
	private final AnimalJpaRepository jpa;
	private final AnimalPersistenceMapper mapper;

	@Override
	public Optional<Animal> findById(UUID id) {
		return jpa.findById(id).map(mapper::toDomain);
	}

	@Override
	public Optional<Animal> findByTagNumber(String tagNumber) {
		if (tagNumber == null || tagNumber.isBlank()) {
			return Optional.empty();
		}
		return jpa.findByTagNumber(tagNumber.trim()).map(mapper::toDomain);
	}

	@Override
	public List<Animal> findByFarmId(UUID farmId) {
		return jpa.findAllByFarmId(farmId).stream().map(mapper::toDomain).toList();
	}

	@Override
	public void save(Animal animal) {
		jpa.save(mapper.toEntity(animal));
	}
}
