package com.shabic.livestock.application.service;

import com.shabic.livestock.domain.repository.DeletedFarmRepository;
import com.shabic.livestock.infrastructure.client.FarmServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FarmLookupServiceTest {

	@Mock private DeletedFarmRepository deletedFarmRepo;
	@Mock private FarmServiceClient farmServiceClient;

	private FarmLookupService service;

	@BeforeEach
	void setUp() {
		service = new FarmLookupService(deletedFarmRepo, farmServiceClient);
	}

	@Test
	void assertFarmExists_rejectsLocallyDeletedFarm_withoutCallingRemote() {
		UUID farmId = UUID.randomUUID();
		when(deletedFarmRepo.isDeleted(farmId)).thenReturn(true);

		assertThatThrownBy(() -> service.assertFarmExists(farmId))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("farm not found");

		verify(farmServiceClient, never()).assertFarmExists(any());
	}

	@Test
	void assertFarmExists_callsFarmService_whenNotDeletedLocally() {
		UUID farmId = UUID.randomUUID();
		when(deletedFarmRepo.isDeleted(farmId)).thenReturn(false);

		service.assertFarmExists(farmId);

		verify(farmServiceClient).assertFarmExists(farmId);
	}
}
