package com.shabic.livestock.api.dto;

import com.shabic.livestock.domain.model.valueobject.AnimalStatus;
import com.shabic.livestock.domain.model.valueobject.Gender;
import com.shabic.livestock.domain.model.valueobject.MethodAcquired;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalResponse {
	private UUID id;
	private String tagNumber;
	private String type;
	private String breed;
	private Gender gender;
	private LocalDate birthDate;
	private AnimalStatus status;
	private UUID farmId;
	private UUID currentLocationId;
	private Instant createdAt;
	private UUID motherAnimalId;
	private UUID shedId;
	private UUID batchId;
	private LocalDate assignDate;
	private MethodAcquired methodAcquired;
	private Set<String> feedTypes;
	private String labelsKeywords;
	private String internalId;
	private String coloring;
	private String additionalTagNumbers;
	private String electronicId;
	private String markingLeft;
	private String markingRight;
	private String description;
}
