package com.shabic.livestock.infrastructure.persistence.adapters;

import com.shabic.livestock.domain.model.AnimalHistoryRecord;
import com.shabic.livestock.domain.repository.AnimalHistoryRepository;
import com.shabic.livestock.infrastructure.persistence.AnimalHistoryPersistenceMapper;
import com.shabic.livestock.infrastructure.persistence.repositories.AnimalHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaAnimalHistoryRepository implements AnimalHistoryRepository {
	private final AnimalHistoryJpaRepository jpa;
	private final AnimalHistoryPersistenceMapper mapper;

	@Override
	public void save(AnimalHistoryRecord record) {
		jpa.save(mapper.toEntity(record));
	}

	@Override
	public List<AnimalHistoryRecord> findByAnimalId(UUID animalId) {
		return jpa.findAllByAnimalIdOrderByCreatedAtAsc(animalId)
				.stream()
				.map(mapper::toDomain)
				.toList();
	}
}

