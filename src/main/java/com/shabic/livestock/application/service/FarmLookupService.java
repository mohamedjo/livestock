package com.shabic.livestock.application.service;

import com.shabic.livestock.application.lookup.FarmLookup;
import com.shabic.livestock.domain.repository.DeletedFarmRepository;
import com.shabic.livestock.infrastructure.client.FarmServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FarmLookupService implements FarmLookup {
	private final DeletedFarmRepository deletedFarmRepo;
	private final FarmServiceClient farmServiceClient;

	@Override
	public void assertFarmExists(UUID farmId) {
		Objects.requireNonNull(farmId, "farmId");
		if (deletedFarmRepo.isDeleted(farmId)) {
			throw new IllegalArgumentException("farm not found");
		}
		farmServiceClient.assertFarmExists(farmId);
	}
}
