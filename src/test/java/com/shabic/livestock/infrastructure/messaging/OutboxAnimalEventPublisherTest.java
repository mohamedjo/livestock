package com.shabic.livestock.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shabic.livestock.config.correlation.CorrelationIdContext;
import com.shabic.livestock.domain.events.AnimalCreated;
import com.shabic.livestock.domain.repository.OutboxRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OutboxAnimalEventPublisherTest {

	@Mock private OutboxRepository outboxRepo;

	@Captor private ArgumentCaptor<String> payloadCaptor;

	private OutboxAnimalEventPublisher publisher;

	@AfterEach
	void tearDown() {
		CorrelationIdContext.clear();
	}

	@BeforeEach
	void setUp() {
		ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
		publisher = new OutboxAnimalEventPublisher(outboxRepo, mapper);
		ReflectionTestUtils.setField(publisher, "animalCreatedTopic", "livestock.animal.created");
	}

	@Test
	void publishAnimalCreated_enqueuesSerializedEventInOutbox() throws Exception {
		UUID eventId = UUID.randomUUID();
		UUID animalId = UUID.randomUUID();
		UUID farmId = UUID.randomUUID();
		Instant timestamp = Instant.parse("2025-01-15T10:00:00Z");
		AnimalCreated event = new AnimalCreated(eventId, animalId, farmId, "Cow", timestamp);
		CorrelationIdContext.set("corr-123");

		publisher.publishAnimalCreated(event);

		verify(outboxRepo).enqueue(
				eq(eventId),
				eq("livestock.animal.created"),
				eq(animalId.toString()),
				payloadCaptor.capture(),
				eq(timestamp),
				eq("corr-123")
		);

		AnimalCreated deserialized = new ObjectMapper()
				.registerModule(new JavaTimeModule())
				.readValue(payloadCaptor.getValue(), AnimalCreated.class);
		assertThat(deserialized.eventId()).isEqualTo(eventId);
		assertThat(deserialized.animalId()).isEqualTo(animalId);
		assertThat(deserialized.farmId()).isEqualTo(farmId);
		assertThat(deserialized.eventType()).isEqualTo("AnimalCreated");
	}
}
