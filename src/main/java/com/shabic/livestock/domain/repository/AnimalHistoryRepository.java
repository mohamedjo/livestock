package com.shabic.livestock.domain.repository;

import com.shabic.livestock.domain.model.AnimalHistoryRecord;

import java.util.List;
import java.util.UUID;

public interface AnimalHistoryRepository {
	void save(AnimalHistoryRecord record);
	List<AnimalHistoryRecord> findByAnimalId(UUID animalId);
}

