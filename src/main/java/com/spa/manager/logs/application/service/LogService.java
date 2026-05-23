package com.spa.manager.logs.application.service;

import com.spa.manager.logs.application.dto.LogResponse;
import com.spa.manager.logs.application.ports.input.ListarLogsUseCase;
import com.spa.manager.logs.application.ports.input.RegistrarLogUseCase;
import com.spa.manager.logs.application.ports.output.LogRepositoryPort;
import com.spa.manager.logs.domain.model.Log;
import com.spa.manager.auth.infrastructure.output.persistence.repository.UsuarioJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogService implements RegistrarLogUseCase, ListarLogsUseCase {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);

    private final LogRepositoryPort logRepository;
    private final UsuarioJpaRepository usuarioRepository;

    public LogService(LogRepositoryPort logRepository,
                      UsuarioJpaRepository usuarioRepository) {
        this.logRepository = logRepository;
        this.usuarioRepository = usuarioRepository;
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

    @Override
    public List<LogResponse> listarRecientes(int limit) {
        return logRepository.findRecientes(limit).stream()
                .map(log -> {
                    String nombreUsuario = null;
                    if (log.getIdUsuario() != null) {
                        nombreUsuario = usuarioRepository.findById(log.getIdUsuario())
                                .map(u -> u.getNombre())
                                .orElse("Usuario eliminado");
                    }
                    return new LogResponse(
                            log.getIdLog(),
                            log.getTipo(),
                            log.getIdUsuario(),
                            nombreUsuario,
                            log.getDescripcion(),
                            log.getIp(),
                            log.getFechaHora()
                    );
                })
                .collect(Collectors.toList());
    }
}