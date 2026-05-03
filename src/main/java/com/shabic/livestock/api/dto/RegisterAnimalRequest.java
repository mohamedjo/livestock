package com.shabic.livestock.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterAnimalRequest {
	/** Optional physical tag / ear tag (plain string). */
	private String tagNumber;

	@NotBlank
	private String type;

	private String breed;

	private String gender;

	private LocalDate birthDate;

	@NotNull
	private UUID farmId;

	private UUID initialLocationId;

	private UUID motherAnimalId;

	private UUID shedId;

	private UUID batchId;

	private LocalDate assignDate;

	private String methodAcquired;

	private List<String> feedTypes;

	private String labelsKeywords;

	private String internalId;

	private String coloring;

	private String additionalTagNumbers;

	private String electronicId;

	private String markingLeft;

	private String markingRight;

	private String description;

	/** UI "Active" maps to domain {@code ALIVE}. */
	private String status;
}
