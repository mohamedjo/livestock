package com.shabic.livestock.infrastructure.persistence.repositories;

import com.shabic.livestock.infrastructure.persistence.entities.AnimalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnimalJpaRepository extends JpaRepository<AnimalEntity, UUID> {
	Optional<AnimalEntity> findByTagNumber(String tagNumber);
	List<AnimalEntity> findAllByFarmId(UUID farmId);
}
