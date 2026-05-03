package com.shabic.livestock.api.mappers;

import com.shabic.livestock.api.dto.AnimalHistoryResponse;
import com.shabic.livestock.api.dto.AnimalResponse;
import com.shabic.livestock.domain.model.AnimalHistoryRecord;
import com.shabic.livestock.domain.model.aggregate.Animal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnimalApiMapper {
	AnimalResponse toResponse(Animal animal);

	AnimalHistoryResponse toResponse(AnimalHistoryRecord record);
}
