package com.spa.manager.logs.infrastructure.output.persistence.repository;

import com.spa.manager.logs.infrastructure.output.persistence.entity.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogJpaRepository extends JpaRepository<LogEntity, Integer> {
}