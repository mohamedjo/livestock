package com.shabic.livestock.domain.events;

import java.time.Instant;

public interface DomainEvent {
	String eventType();
	Instant timestamp();
}
