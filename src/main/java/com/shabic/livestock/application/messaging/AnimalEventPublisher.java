package com.shabic.livestock.application.messaging;

import com.shabic.livestock.domain.events.AnimalCreated;

public interface AnimalEventPublisher {
	void publishAnimalCreated(AnimalCreated event);
}
