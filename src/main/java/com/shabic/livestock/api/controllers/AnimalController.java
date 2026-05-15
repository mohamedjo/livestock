package com.shabic.livestock.api.controllers;

import com.shabic.livestock.api.dto.AnimalHistoryResponse;
import com.shabic.livestock.api.dto.AnimalResponse;
import com.shabic.livestock.api.dto.RegisterAnimalRequest;
import com.shabic.livestock.api.mappers.AnimalApiMapper;
import com.shabic.livestock.api.mappers.AnimalCommandMapper;
import com.shabic.livestock.application.service.AnimalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/animals")
@RequiredArgsConstructor
public class AnimalController {
	private final AnimalService animalService;
	private final AnimalCommandMapper commandMapper;
	private final AnimalApiMapper apiMapper;

	@PostMapping
	@PreAuthorize("hasAnyRole('FARM_USER', 'FARM_ADMIN')")
	@ResponseStatus(HttpStatus.CREATED)
	public UUID register(@Valid @RequestBody RegisterAnimalRequest request) {
		return animalService.register(commandMapper.toCommand(request));
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('FARM_USER', 'FARM_ADMIN')")
	public AnimalResponse update(@PathVariable("id") UUID animalId, @Valid @RequestBody RegisterAnimalRequest request) {
		return apiMapper.toResponse(animalService.update(commandMapper.toUpdateCommand(animalId, request)));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole( 'FARM_ADMIN', 'FARM_USER')")
	public List<AnimalResponse> getByFarm(@RequestParam("farmId") UUID farmId) {
		return animalService.getByFarm(farmId)
				.stream()
				.map(apiMapper::toResponse)
				.toList();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'FARM_ADMIN', 'FARM_USER')")
	public AnimalResponse getDetails(@PathVariable("id") UUID animalId) {
		return apiMapper.toResponse(animalService.getDetails(animalId));
	}

	@GetMapping("/{id}/history")
	@PreAuthorize("hasAnyRole('ADMIN', 'FARM_ADMIN', 'FARM_USER')")
	public List<AnimalHistoryResponse> history(@PathVariable("id") UUID animalId) {
		return animalService.history(animalId)
				.stream()
				.map(apiMapper::toResponse)
				.toList();
	}
}
