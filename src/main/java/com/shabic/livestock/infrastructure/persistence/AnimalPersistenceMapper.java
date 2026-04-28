package com.shabic.livestock.infrastructure.persistence;

import com.shabic.livestock.domain.model.Animal;
import com.shabic.livestock.domain.model.TagNumber;
import com.shabic.livestock.infrastructure.persistence.entities.AnimalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AnimalPersistenceMapper {
	@Mapping(target = "tagNumber", source = "tagNumber", qualifiedByName = "tagNumberToString")
	AnimalEntity toEntity(Animal animal);

	default Animal toDomain(AnimalEntity entity) {
		return Animal.rehydrate(
				entity.getId(),
				new TagNumber(entity.getTagNumber()),
				entity.getType(),
				entity.getBreed(),
				entity.getGender(),
				entity.getBirthDate(),
				entity.getFarmId(),
				entity.getCurrentLocationId(),
				entity.getStatus(),
				entity.getCreatedAt()
		);
	}

	@Named("tagNumberToString")
	static String tagNumberToString(TagNumber tagNumber) {
		return tagNumber == null ? null : tagNumber.value();
	}

	@Named("stringToTagNumber")
	static TagNumber stringToTagNumber(String value) {
		return value == null ? null : new TagNumber(value);
	}
}

