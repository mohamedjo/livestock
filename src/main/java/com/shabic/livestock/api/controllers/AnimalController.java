package com.shabic.livestock.api.controllers;

import com.shabic.livestock.api.dto.AnimalHistoryResponse;
import com.shabic.livestock.api.dto.AnimalResponse;
import com.shabic.livestock.api.dto.MoveAnimalRequest;
import com.shabic.livestock.api.dto.RegisterAnimalRequest;
import com.shabic.livestock.api.mappers.AnimalCommandMapper;
import com.shabic.livestock.application.handlers.MoveAnimalHandler;
import com.shabic.livestock.application.handlers.QueryHandlers;
import com.shabic.livestock.application.handlers.RegisterAnimalHandler;
import com.shabic.livestock.application.handlers.SellAnimalHandler;
import com.shabic.livestock.application.queries.GetAnimalDetailsQuery;
import com.shabic.livestock.application.queries.GetAnimalHistoryQuery;
import com.shabic.livestock.application.queries.GetAnimalsByFarmQuery;
import com.shabic.livestock.api.mappers.AnimalApiMapper;
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
	private final RegisterAnimalHandler registerHandler;
	private final MoveAnimalHandler moveHandler;
	private final SellAnimalHandler sellHandler;
	private final QueryHandlers queryHandlers;
	private final AnimalCommandMapper commandMapper;
	private final AnimalApiMapper apiMapper;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UUID register(@Valid @RequestBody RegisterAnimalRequest req) {
		return registerHandler.handle(commandMapper.toCommand(req));
	}

	@PostMapping("/{id}/move")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void move(@PathVariable("id") UUID animalId, @Valid @RequestBody MoveAnimalRequest req) {
		moveHandler.handle(commandMapper.toCommand(animalId, req));
	}

	@PostMapping("/{id}/sell")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void sell(@PathVariable("id") UUID animalId) {
		sellHandler.handle(commandMapper.toSellCommand(animalId));
	}

	@GetMapping
	public List<AnimalResponse> getByFarm(@RequestParam("farmId") UUID farmId) {
		return queryHandlers.handle(GetAnimalsByFarmQuery.builder().farmId(farmId).build())
				.stream()
				.map(apiMapper::toResponse)
				.toList();
	}

	@GetMapping("/{id}")
	public AnimalResponse getDetails(@PathVariable("id") UUID animalId) {
		return apiMapper.toResponse(
				queryHandlers.handle(GetAnimalDetailsQuery.builder().animalId(animalId).build())
		);
	}

	@GetMapping("/{id}/history")
	public List<AnimalHistoryResponse> history(@PathVariable("id") UUID animalId) {
		return queryHandlers.handle(GetAnimalHistoryQuery.builder().animalId(animalId).build())
				.stream()
				.map(apiMapper::toResponse)
				.toList();
	}
}

