package com.shabic.livestock.infrastructure.persistence.adapters;

import com.shabic.livestock.domain.model.AnimalHistoryRecord;
import com.shabic.livestock.domain.repository.AnimalHistoryRepository;
import com.shabic.livestock.infrastructure.persistence.entities.AnimalHistoryEntity;
import com.shabic.livestock.infrastructure.persistence.repositories.AnimalHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaAnimalHistoryRepository implements AnimalHistoryRepository {
	private final AnimalHistoryJpaRepository jpa;

	@Override
	public void save(AnimalHistoryRecord record) {
		jpa.save(AnimalHistoryEntity.builder()
				.id(record.id())
				.animalId(record.animalId())
				.eventType(record.eventType())
				.eventData(record.eventData())
				.createdAt(record.createdAt())
				.build());
	}

	@Override
	public List<AnimalHistoryRecord> findByAnimalId(UUID animalId) {
		return jpa.findAllByAnimalIdOrderByCreatedAtAsc(animalId)
				.stream()
				.map(e -> new AnimalHistoryRecord(
						e.getId(),
						e.getAnimalId(),
						e.getEventType(),
						e.getEventData(),
						e.getCreatedAt()
				))
				.toList();
	}
}

