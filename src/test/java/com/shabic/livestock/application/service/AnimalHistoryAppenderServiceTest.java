package com.shabic.livestock.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shabic.livestock.domain.events.AnimalCreated;
import com.shabic.livestock.domain.model.AnimalHistoryRecord;
import com.shabic.livestock.domain.repository.AnimalHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnimalHistoryAppenderServiceTest {

	@Mock private AnimalHistoryRepository historyRepo;
	@Mock private ObjectMapper objectMapper;

	@Captor private ArgumentCaptor<AnimalHistoryRecord> recordCaptor;

	private AnimalHistoryAppenderService service;

	@BeforeEach
	void setUp() {
		service = new AnimalHistoryAppenderService(historyRepo, objectMapper);
	}

	@Test
	void append_serializesPayloadAndSavesHistoryRecord() throws Exception {
		UUID animalId = UUID.randomUUID();
		Instant now = Instant.parse("2024-06-01T12:00:00Z");
		AnimalCreated payload = new AnimalCreated(
				UUID.randomUUID(),
				animalId,
				UUID.randomUUID(),
				"Cow",
				now
		);
		ObjectMapper realMapper = new ObjectMapper().registerModule(new JavaTimeModule());
		String expectedJson = realMapper.writeValueAsString(payload);
		when(objectMapper.writeValueAsString(payload)).thenReturn(expectedJson);

		service.append(animalId, "AnimalCreated", payload, now);

		verify(historyRepo).save(recordCaptor.capture());
		AnimalHistoryRecord saved = recordCaptor.getValue();
		assertThat(saved.id()).isNotNull();
		assertThat(saved.animalId()).isEqualTo(animalId);
		assertThat(saved.eventType()).isEqualTo("AnimalCreated");
		assertThat(saved.createdAt()).isEqualTo(now);
		assertThat(saved.eventData()).isEqualTo(expectedJson);
	}

	@Test
	void append_wrapsSerializationFailure() throws Exception {
		UUID animalId = UUID.randomUUID();
		Instant now = Instant.now();
		JsonProcessingException cause = new JsonProcessingException("cannot serialize") {};
		when(objectMapper.writeValueAsString(any())).thenThrow(cause);

		assertThatThrownBy(() -> service.append(animalId, "AnimalFed", Map.of(), now))
				.isInstanceOf(RuntimeException.class)
				.hasMessage("Failed to write animal_history event")
				.hasCause(cause);

		verify(historyRepo, never()).save(any());
	}

	@Test
	void append_wrapsRepositoryFailure() throws Exception {
		UUID animalId = UUID.randomUUID();
		Instant now = Instant.now();
		when(objectMapper.writeValueAsString(any())).thenReturn("{}");
		RuntimeException cause = new RuntimeException("db unavailable");
		doThrow(cause).when(historyRepo).save(any());

		assertThatThrownBy(() -> service.append(animalId, "AnimalFed", Map.of(), now))
				.isInstanceOf(RuntimeException.class)
				.hasMessage("Failed to write animal_history event")
				.hasCause(cause);
	}
}
