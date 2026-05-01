package com.shabic.livestock.infrastructure.persistence;

import com.shabic.livestock.domain.model.AnimalHistoryRecord;
import com.shabic.livestock.infrastructure.persistence.entities.AnimalHistoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnimalHistoryPersistenceMapper {
	AnimalHistoryEntity toEntity(AnimalHistoryRecord record);
	AnimalHistoryRecord toDomain(AnimalHistoryEntity entity);
}

