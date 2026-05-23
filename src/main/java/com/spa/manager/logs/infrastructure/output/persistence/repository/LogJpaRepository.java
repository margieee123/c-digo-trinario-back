package com.spa.manager.logs.infrastructure.output.persistence.repository;

import com.spa.manager.logs.infrastructure.output.persistence.entity.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LogJpaRepository extends JpaRepository<LogEntity, Integer> {

    @Query("SELECT l FROM LogEntity l ORDER BY l.fechaHora DESC LIMIT :limit")
    List<LogEntity> findRecientes(@Param("limit") int limit);
}