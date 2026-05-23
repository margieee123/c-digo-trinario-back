package com.spa.manager.usuarioadmin.infrastructure.output.persistence.adapter;

import com.spa.manager.usuarioadmin.application.ports.output.TerapeutaServicioRepositoryPort;
import com.spa.manager.usuarioadmin.domain.model.TerapeutaServicio;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.mapper.TerapeutaServicioMapper;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.repository.TerapeutaServicioJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class TerapeutaServicioRepositoryAdapter implements TerapeutaServicioRepositoryPort {

    private final TerapeutaServicioJpaRepository jpaRepository;
    private final TerapeutaServicioMapper mapper;

    public TerapeutaServicioRepositoryAdapter(TerapeutaServicioJpaRepository jpaRepository,
                                              TerapeutaServicioMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(TerapeutaServicio ts) {
        jpaRepository.save(mapper.toEntity(ts));
    }

    @Override
    @Transactional
    public void delete(Integer idTerapeuta, Integer idServicio) {
        jpaRepository.deleteByIdTerapeutaAndIdServicio(idTerapeuta, idServicio);
    }

    @Override
    public List<Integer> findServiciosByIdTerapeuta(Integer idTerapeuta) {
        return jpaRepository.findServicioIdsByIdTerapeuta(idTerapeuta);
    }

    @Override
    public List<Integer> findTerapeutasByIdServicio(Integer idServicio) {
        return jpaRepository.findTerapeutaIdsByIdServicio(idServicio);
    }

    @Override
    public boolean exists(Integer idTerapeuta, Integer idServicio) {
        return jpaRepository.existsByIdTerapeutaAndIdServicio(idTerapeuta, idServicio);
    }
}