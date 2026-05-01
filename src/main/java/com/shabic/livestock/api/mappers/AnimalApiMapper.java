package com.shabic.livestock.api.mappers;

import com.shabic.livestock.api.dto.AnimalHistoryResponse;
import com.shabic.livestock.api.dto.AnimalResponse;
import com.shabic.livestock.domain.model.AnimalHistoryRecord;
import com.shabic.livestock.domain.model.aggregate.Animal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AnimalApiMapper {
	@Mapping(target = "tagNumber", source = "tagNumber", qualifiedByName = "tagNumberToString")
	AnimalResponse toResponse(Animal animal);

	AnimalHistoryResponse toResponse(AnimalHistoryRecord record);

	@Named("tagNumberToString")
	static String tagNumberToString(com.shabic.livestock.domain.model.valueobject.TagNumber tagNumber) {
		return tagNumber == null ? null : tagNumber.value();
	}
}

