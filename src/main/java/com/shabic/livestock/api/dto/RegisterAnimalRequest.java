package com.shabic.livestock.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterAnimalRequest {
	@NotBlank
	private String tagNumber;

	@NotBlank
	private String type;

	private String breed;

	private String gender;

	private LocalDate birthDate;

	@NotNull
	private UUID farmId;

	private UUID initialLocationId;
}
