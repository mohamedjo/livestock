package com.shabic.livestock.infrastructure.messaging;

import com.shabic.livestock.config.correlation.CorrelationId;
import com.shabic.livestock.domain.repository.OutboxRepository;
import com.shabic.livestock.domain.repository.OutboxRepository.OutboxMessage;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxEventDispatcherTest {

	@Mock private OutboxRepository outboxRepo;
	@Mock private KafkaTemplate<String, String> kafkaTemplate;

	@Captor private ArgumentCaptor<Instant> publishedAtCaptor;
	@Captor private ArgumentCaptor<ProducerRecord<String, String>> producerRecordCaptor;

	private OutboxEventDispatcher dispatcher;

	@BeforeEach
	void setUp() {
		dispatcher = new OutboxEventDispatcher(outboxRepo, kafkaTemplate);
		ReflectionTestUtils.setField(dispatcher, "batchSize", 50);
	}

	@Test
	void dispatchPendingEvents_publishesToKafkaAndMarksPublished() {
		UUID outboxId = UUID.randomUUID();
		OutboxMessage message = new OutboxMessage(
				outboxId,
				"livestock.animal.created",
				"animal-key",
				"{\"eventType\":\"AnimalCreated\"}",
				"corr-456"
		);
		when(outboxRepo.findUnpublished(50)).thenReturn(List.of(message));

		dispatcher.dispatchPendingEvents();

		verify(kafkaTemplate).send(producerRecordCaptor.capture());
		ProducerRecord<String, String> record = producerRecordCaptor.getValue();
		assertThat(record.topic()).isEqualTo("livestock.animal.created");
		assertThat(record.key()).isEqualTo("animal-key");
		assertThat(record.value()).isEqualTo(message.payload());
		assertThat(record.headers().lastHeader(CorrelationId.HEADER_NAME).value())
				.isEqualTo("corr-456".getBytes());
		verify(outboxRepo).markPublished(eq(outboxId), publishedAtCaptor.capture());
		verifyNoMoreInteractions(kafkaTemplate);
	}
}
