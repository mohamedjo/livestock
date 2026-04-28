package com.shabic.livestock.api.mappers;

import com.shabic.livestock.api.dto.MoveAnimalRequest;
import com.shabic.livestock.api.dto.RegisterAnimalRequest;
import com.shabic.livestock.application.commands.MoveAnimalCommand;
import com.shabic.livestock.application.commands.RegisterAnimalCommand;
import com.shabic.livestock.application.commands.SellAnimalCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AnimalCommandMapper {
	RegisterAnimalCommand toCommand(RegisterAnimalRequest request);

	@Mapping(target = "animalId", source = "animalId")
	@Mapping(target = "toLocationId", source = "request.toLocationId")
	MoveAnimalCommand toCommand(UUID animalId, MoveAnimalRequest request);

	@Mapping(target = "animalId", source = "animalId")
	SellAnimalCommand toSellCommand(UUID animalId);
}

