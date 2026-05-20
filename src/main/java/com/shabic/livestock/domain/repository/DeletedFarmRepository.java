package com.shabic.livestock.domain.repository;

import java.time.Instant;
import java.util.UUID;

public interface DeletedFarmRepository {
	boolean isDeleted(UUID farmId);

	void markDeleted(UUID farmId, Instant deletedAt);
}
