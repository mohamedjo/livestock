package com.shabic.livestock.api.mappers;

import com.shabic.livestock.api.dto.RegisterAnimalRequest;
import com.shabic.livestock.application.command.RegisterAnimalCommand;
import com.shabic.livestock.application.command.UpdateAnimalCommand;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AnimalCommandMapper {

	RegisterAnimalCommand toCommand(RegisterAnimalRequest request);

	default UpdateAnimalCommand toUpdateCommand(UUID animalId, RegisterAnimalRequest request) {
		return new UpdateAnimalCommand(animalId, toCommand(request));
	}
}
