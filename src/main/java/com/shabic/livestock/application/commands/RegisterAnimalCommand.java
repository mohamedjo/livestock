package com.shabic.livestock.application.commands;

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
public class RegisterAnimalCommand {
	private String tagNumber;
	private String type;
	private String breed;
	private String gender; // MALE/FEMALE/UNKNOWN
	private LocalDate birthDate;
	private UUID farmId;
	private UUID initialLocationId;
}
