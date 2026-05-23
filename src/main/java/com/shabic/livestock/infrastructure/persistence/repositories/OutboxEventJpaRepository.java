package com.shabic.livestock.infrastructure.persistence.repositories;

import com.shabic.livestock.infrastructure.persistence.entities.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventEntity, UUID> {
	@Query("""
			SELECT e FROM OutboxEventEntity e
			WHERE e.publishedAt IS NULL
			ORDER BY e.createdAt ASC
			""")
	List<OutboxEventEntity> findUnpublished(org.springframework.data.domain.Pageable pageable);
}
