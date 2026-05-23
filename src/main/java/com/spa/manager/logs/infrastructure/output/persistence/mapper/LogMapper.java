package com.spa.manager.logs.infrastructure.output.persistence.mapper;

import com.spa.manager.logs.domain.model.Log;
import com.spa.manager.logs.infrastructure.output.persistence.entity.LogEntity;
import org.springframework.stereotype.Component;

@Component
public class LogMapper {

    public LogEntity toEntity(Log log) {
        LogEntity entity = new LogEntity();
        entity.setTipo(log.getTipo());
        entity.setIdUsuario(log.getIdUsuario());
        entity.setDescripcion(log.getDescripcion());
        entity.setIp(log.getIp());
        entity.setFechaHora(log.getFechaHora());
        return entity;
    }

    public Log toDomain(LogEntity entity) {
        Log log = new Log();
        log.setIdLog(entity.getIdLog());
        log.setTipo(entity.getTipo());
        log.setIdUsuario(entity.getIdUsuario());
        log.setDescripcion(entity.getDescripcion());
        log.setIp(entity.getIp());
        log.setFechaHora(entity.getFechaHora());
        return log;
    }
}