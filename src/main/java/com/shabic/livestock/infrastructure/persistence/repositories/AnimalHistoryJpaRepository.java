package com.shabic.livestock.infrastructure.persistence.repositories;

import com.shabic.livestock.infrastructure.persistence.entities.AnimalHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnimalHistoryJpaRepository extends JpaRepository<AnimalHistoryEntity, UUID> {
	List<AnimalHistoryEntity> findAllByAnimalIdOrderByCreatedAtAsc(UUID animalId);
}
