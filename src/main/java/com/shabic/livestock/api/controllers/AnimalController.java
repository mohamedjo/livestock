package com.shabic.livestock.api.controllers;

import com.shabic.livestock.api.dto.AnimalHistoryResponse;
import com.shabic.livestock.api.dto.AnimalResponse;
import com.shabic.livestock.api.dto.MoveAnimalRequest;
import com.shabic.livestock.api.dto.RegisterAnimalRequest;
import com.shabic.livestock.api.mappers.AnimalApiMapper;
import com.shabic.livestock.api.mappers.AnimalCommandMapper;
import com.shabic.livestock.application.service.AnimalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
	@ResponseStatus(HttpStatus.CREATED)
	public UUID register(@Valid @RequestBody RegisterAnimalRequest request) {
		return animalService.register(commandMapper.toCommand(request));
	}

	@PutMapping("/{id}")
	public AnimalResponse update(@PathVariable("id") UUID animalId, @Valid @RequestBody RegisterAnimalRequest request) {
		return apiMapper.toResponse(animalService.update(commandMapper.toUpdateCommand(animalId, request)));
	}

	@PostMapping("/{id}/move")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void move(@PathVariable("id") UUID animalId, @Valid @RequestBody MoveAnimalRequest request) {
		animalService.move(commandMapper.toMoveCommand(animalId, request));
	}

	@PostMapping("/{id}/sell")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void sell(@PathVariable("id") UUID animalId) {
		animalService.sell(commandMapper.toSellCommand(animalId));
	}

	@GetMapping
	public List<AnimalResponse> getByFarm(@RequestParam("farmId") UUID farmId) {
		return animalService.getByFarm(farmId)
				.stream()
				.map(apiMapper::toResponse)
				.toList();
	}

	@GetMapping("/{id}")
	public AnimalResponse getDetails(@PathVariable("id") UUID animalId) {
		return apiMapper.toResponse(animalService.getDetails(animalId));
	}

	@GetMapping("/{id}/history")
	public List<AnimalHistoryResponse> history(@PathVariable("id") UUID animalId) {
		return animalService.history(animalId)
				.stream()
				.map(apiMapper::toResponse)
				.toList();
	}
}
