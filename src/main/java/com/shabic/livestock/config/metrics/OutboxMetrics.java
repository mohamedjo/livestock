package com.shabic.livestock.config.metrics;

import com.shabic.livestock.infrastructure.persistence.repositories.OutboxEventJpaRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxMetrics {
	private final OutboxEventJpaRepository outboxRepo;
	private final MeterRegistry meterRegistry;

	@PostConstruct
	void register() {
		Gauge.builder("livestock.outbox.unpublished", outboxRepo, OutboxEventJpaRepository::countUnpublished)
				.description("Number of outbox events waiting to be published to Kafka")
				.register(meterRegistry);
	}
}
