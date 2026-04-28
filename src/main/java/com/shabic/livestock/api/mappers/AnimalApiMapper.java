package com.shabic.livestock.api.mappers;

import com.shabic.livestock.api.dto.AnimalHistoryResponse;
import com.shabic.livestock.api.dto.AnimalResponse;
import com.shabic.livestock.application.readmodel.AnimalHistoryView;
import com.shabic.livestock.application.readmodel.AnimalView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnimalApiMapper {
	AnimalResponse toResponse(AnimalView view);
	AnimalHistoryResponse toResponse(AnimalHistoryView view);
}

