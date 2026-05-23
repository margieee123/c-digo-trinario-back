package com.spa.manager.shared.configuracion.application.service;

import com.spa.manager.shared.configuracion.application.dto.ConfiguracionSpaDto;
import com.spa.manager.shared.configuracion.infrastructure.output.persistence.entity.ConfiguracionSpaEntity;
import com.spa.manager.shared.configuracion.infrastructure.output.persistence.repository.ConfiguracionSpaJpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ConfiguracionSpaService {

    private final ConfiguracionSpaJpaRepository repository;

    public ConfiguracionSpaService(ConfiguracionSpaJpaRepository repository) {
        this.repository = repository;
    }

    public ConfiguracionSpaDto obtener() {
        return repository.findById(1)
                .map(e -> new ConfiguracionSpaDto(e.getNombre(), e.getDireccion(), e.getTelefono(), e.getEmail()))
                .orElse(new ConfiguracionSpaDto("Spa Manager", "", "", ""));
    }

    public ConfiguracionSpaDto guardar(ConfiguracionSpaDto dto) {
        ConfiguracionSpaEntity entity = repository.findById(1)
                .orElse(new ConfiguracionSpaEntity());
        entity.setId(1);
        entity.setNombre(dto.getNombre() != null ? dto.getNombre() : "Spa Manager");
        entity.setDireccion(dto.getDireccion());
        entity.setTelefono(dto.getTelefono());
        entity.setEmail(dto.getEmail());
        repository.save(entity);
        return new ConfiguracionSpaDto(entity.getNombre(), entity.getDireccion(), entity.getTelefono(), entity.getEmail());
    }
}