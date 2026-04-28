package com.shabic.livestock.application.handlers;

import com.shabic.livestock.application.mappers.AnimalReadModelMapper;
import com.shabic.livestock.application.queries.GetAnimalDetailsQuery;
import com.shabic.livestock.application.queries.GetAnimalHistoryQuery;
import com.shabic.livestock.application.queries.GetAnimalsByFarmQuery;
import com.shabic.livestock.application.readmodel.AnimalHistoryView;
import com.shabic.livestock.application.readmodel.AnimalView;
import com.shabic.livestock.infrastructure.persistence.entities.AnimalEntity;
import com.shabic.livestock.infrastructure.persistence.entities.AnimalHistoryEntity;
import com.shabic.livestock.infrastructure.persistence.repositories.AnimalHistoryJpaRepository;
import com.shabic.livestock.infrastructure.persistence.repositories.AnimalJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueryHandlers {
	private final AnimalJpaRepository animalRepo;
	private final AnimalHistoryJpaRepository historyRepo;
	private final AnimalReadModelMapper mapper;

	@Transactional(readOnly = true)
	public List<AnimalView> handle(GetAnimalsByFarmQuery query) {
		List<AnimalEntity> animals = animalRepo.findAllByFarmId(query.getFarmId());
		return animals.stream().map(mapper::toView).toList();
	}

	@Transactional(readOnly = true)
	public AnimalView handle(GetAnimalDetailsQuery query) {
		AnimalEntity entity = animalRepo.findById(query.getAnimalId())
				.orElseThrow(() -> new IllegalArgumentException("animal not found"));
		return mapper.toView(entity);
	}

	@Transactional(readOnly = true)
	public List<AnimalHistoryView> handle(GetAnimalHistoryQuery query) {
		List<AnimalHistoryEntity> history = historyRepo.findAllByAnimalIdOrderByCreatedAtAsc(query.getAnimalId());
		return history.stream().map(mapper::toView).toList();
	}
}
