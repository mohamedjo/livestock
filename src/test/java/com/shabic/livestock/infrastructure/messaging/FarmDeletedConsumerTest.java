package com.shabic.livestock.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shabic.livestock.domain.repository.DeletedFarmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FarmDeletedConsumerTest {

	@Mock private DeletedFarmRepository deletedFarmRepo;

	@Captor private ArgumentCaptor<Instant> deletedAtCaptor;

	private FarmDeletedConsumer consumer;

	@BeforeEach
	void setUp() {
		ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
		consumer = new FarmDeletedConsumer(mapper, deletedFarmRepo);
	}

	@Test
	void onFarmDeleted_marksFarmDeleted() {
		UUID farmId = UUID.randomUUID();
		Instant deletedAt = Instant.parse("2024-06-01T12:00:00Z");
		String payload = """
				{
				  "farmId": "%s",
				  "timestamp": "%s",
				  "eventType": "FarmDeleted"
				}
				""".formatted(farmId, deletedAt);

		consumer.onFarmDeleted(payload);

		verify(deletedFarmRepo).markDeleted(eq(farmId), deletedAtCaptor.capture());
		assertThat(deletedAtCaptor.getValue()).isEqualTo(deletedAt);
	}
}
