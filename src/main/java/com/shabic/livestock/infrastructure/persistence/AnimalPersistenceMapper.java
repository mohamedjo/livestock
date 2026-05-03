package com.shabic.livestock.infrastructure.persistence;

import com.shabic.livestock.domain.model.aggregate.Animal;
import com.shabic.livestock.infrastructure.persistence.entities.AnimalEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.HashSet;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface AnimalPersistenceMapper {
	@Mapping(target = "feedTypes", source = "feedTypes", qualifiedByName = "copyFeedTypes")
	AnimalEntity toEntity(Animal animal);

	default Animal toDomain(AnimalEntity entity) {
		Set<String> feeds = entity.getFeedTypes() == null ? Set.of() : Set.copyOf(entity.getFeedTypes());
		return Animal.rehydrate(
				entity.getId(),
				entity.getTagNumber(),
				entity.getType(),
				entity.getBreed(),
				entity.getGender(),
				entity.getBirthDate(),
				entity.getFarmId(),
				entity.getMotherAnimalId(),
				entity.getShedId(),
				entity.getBatchId(),
				entity.getAssignDate(),
				entity.getMethodAcquired(),
				feeds,
				entity.getLabelsKeywords(),
				entity.getInternalId(),
				entity.getColoring(),
				entity.getAdditionalTagNumbers(),
				entity.getElectronicId(),
				entity.getMarkingLeft(),
				entity.getMarkingRight(),
				entity.getDescription(),
				entity.getCurrentLocationId(),
				entity.getStatus(),
				entity.getCreatedAt()
		);
	}

	@Named("copyFeedTypes")
	static Set<String> copyFeedTypes(Set<String> feedTypes) {
		return feedTypes == null ? new HashSet<>() : new HashSet<>(feedTypes);
	}
}
