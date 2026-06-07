package com.shabic.livestock.infrastructure.messaging;

import com.shabic.livestock.config.correlation.CorrelationIdKafka;
import com.shabic.livestock.domain.repository.OutboxRepository;
import com.shabic.livestock.domain.repository.OutboxRepository.OutboxMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxEventDispatcher {
	private final OutboxRepository outboxRepo;
	private final KafkaTemplate<String, String> kafkaTemplate;

	@Value("${livestock.outbox.dispatch.batch-size:50}")
	private int batchSize;

	@Scheduled(fixedDelayString = "${livestock.outbox.dispatch.fixed-delay-ms:2000}")
	@Transactional
	public void dispatchPendingEvents() {
		List<OutboxMessage> pending = outboxRepo.findUnpublished(batchSize);
		Instant publishedAt = Instant.now();
		for (OutboxMessage message : pending) {
			kafkaTemplate.send(CorrelationIdKafka.producerRecord(
					message.topic(),
					message.messageKey(),
					message.payload(),
					message.correlationId()));
			outboxRepo.markPublished(message.id(), publishedAt);
		}
	}
}
