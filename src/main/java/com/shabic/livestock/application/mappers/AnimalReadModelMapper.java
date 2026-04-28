package com.shabic.livestock.application.mappers;

import com.shabic.livestock.application.readmodel.AnimalHistoryView;
import com.shabic.livestock.application.readmodel.AnimalView;
import com.shabic.livestock.infrastructure.persistence.entities.AnimalEntity;
import com.shabic.livestock.infrastructure.persistence.entities.AnimalHistoryEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnimalReadModelMapper {
	AnimalView toView(AnimalEntity entity);
	AnimalHistoryView toView(AnimalHistoryEntity entity);
}

