package com.shabic.livestock.application.readmodel;

import com.shabic.livestock.domain.model.AnimalStatus;
import com.shabic.livestock.domain.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnimalView {
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
}

