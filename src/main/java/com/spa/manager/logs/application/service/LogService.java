package com.spa.manager.logs.application.service;

import com.spa.manager.logs.application.ports.input.RegistrarLogUseCase;
import com.spa.manager.logs.application.ports.output.LogRepositoryPort;
import com.spa.manager.logs.domain.model.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LogService implements RegistrarLogUseCase {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    private final LogRepositoryPort logRepository;

    public LogService(LogRepositoryPort logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public void registrar(Log log) {
        logger.info("[{}] Usuario ID: {} | {} | IP: {}",
                log.getTipo(),
                log.getIdUsuario() != null ? log.getIdUsuario() : "N/A",
                log.getDescripcion(),
                log.getIp() != null ? log.getIp() : "N/A");

        logRepository.save(log);
    }
}