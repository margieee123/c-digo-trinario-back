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
}