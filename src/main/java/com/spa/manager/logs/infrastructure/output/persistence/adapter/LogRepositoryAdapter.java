package com.spa.manager.logs.infrastructure.output.persistence.adapter;

import com.spa.manager.logs.application.ports.output.LogRepositoryPort;
import com.spa.manager.logs.domain.model.Log;
import com.spa.manager.logs.infrastructure.output.persistence.mapper.LogMapper;
import com.spa.manager.logs.infrastructure.output.persistence.repository.LogJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class LogRepositoryAdapter implements LogRepositoryPort {

    private final LogJpaRepository jpaRepository;
    private final LogMapper mapper;

    public LogRepositoryAdapter(LogJpaRepository jpaRepository, LogMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(Log log) {
        jpaRepository.save(mapper.toEntity(log));
    }
}