package com.spa.manager.logs.infrastructure.output.persistence.adapter;

import com.spa.manager.logs.application.ports.output.LogRepositoryPort;
import com.spa.manager.logs.domain.model.Log;
import com.spa.manager.logs.infrastructure.output.persistence.mapper.LogMapper;
import com.spa.manager.logs.infrastructure.output.persistence.repository.LogJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<Log> findRecientes(int limit) {
        return jpaRepository.findRecientes(limit).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}