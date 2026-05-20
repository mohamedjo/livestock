package com.shabic.livestock.infrastructure.persistence.adapters;

import com.shabic.livestock.domain.repository.DeletedFarmRepository;
import com.shabic.livestock.infrastructure.persistence.entities.DeletedFarmEntity;
import com.shabic.livestock.infrastructure.persistence.repositories.DeletedFarmJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaDeletedFarmRepository implements DeletedFarmRepository {
	private final DeletedFarmJpaRepository jpa;

	@Override
	public boolean isDeleted(UUID farmId) {
		return jpa.existsById(farmId);
	}

	@Override
	public void markDeleted(UUID farmId, Instant deletedAt) {
		jpa.save(DeletedFarmEntity.builder()
				.farmId(farmId)
				.deletedAt(deletedAt)
				.build());
	}
}
