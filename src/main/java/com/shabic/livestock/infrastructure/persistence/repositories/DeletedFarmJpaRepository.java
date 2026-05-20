package com.shabic.livestock.infrastructure.persistence.repositories;

import com.shabic.livestock.infrastructure.persistence.entities.DeletedFarmEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeletedFarmJpaRepository extends JpaRepository<DeletedFarmEntity, UUID> {
}
