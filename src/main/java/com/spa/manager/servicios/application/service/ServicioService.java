package com.spa.manager.servicios.application.service;

import com.spa.manager.servicios.application.dto.ServicioRequest;
import com.spa.manager.servicios.application.dto.ServicioResponse;
import com.spa.manager.servicios.application.ports.input.*;
import com.spa.manager.servicios.application.ports.output.ServicioRepositoryPort;
import com.spa.manager.servicios.domain.exception.ServicioNoEncontradoException;
import com.spa.manager.servicios.domain.model.EstadoServicio;
import com.spa.manager.servicios.domain.model.Servicio;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioService implements CrearServicioUseCase, ListarServiciosUseCase,
        ObtenerServicioUseCase, ActualizarServicioUseCase, EliminarServicioUseCase {

    private final ServicioRepositoryPort servicioRepository;

    public ServicioService(ServicioRepositoryPort servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    @Override
    public ServicioResponse crear(ServicioRequest request) {
        Servicio servicio = new Servicio();
        servicio.setNombre(request.getNombre());
        servicio.setDescripcion(request.getDescripcion());
        servicio.setPrecio(request.getPrecio());
        servicio.setDuracionMinutos(request.getDuracionMinutos());
        servicio.setEstado(EstadoServicio.activo);

        Servicio guardado = servicioRepository.save(servicio);
        return toResponse(guardado);
    }

    @Override
    public List<ServicioResponse> listar() {
        return servicioRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ServicioResponse> buscarPorNombre(String nombre) {
        return servicioRepository.buscarPorNombre(nombre)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ServicioResponse obtener(Integer id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ServicioNoEncontradoException("Servicio no encontrado."));
        return toResponse(servicio);
    }

    @Override
    public ServicioResponse actualizar(Integer id, ServicioRequest request) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ServicioNoEncontradoException("Servicio no encontrado."));

        servicio.setNombre(request.getNombre());
        servicio.setDescripcion(request.getDescripcion());
        servicio.setPrecio(request.getPrecio());
        servicio.setDuracionMinutos(request.getDuracionMinutos());

        Servicio actualizado = servicioRepository.save(servicio);
        return toResponse(actualizado);
    }

    @Override
    public void eliminar(Integer id) {
        if (!servicioRepository.existsById(id)) {
            throw new ServicioNoEncontradoException("Servicio no encontrado.");
        }
        servicioRepository.deleteById(id);
    }

    private ServicioResponse toResponse(Servicio servicio) {
        return new ServicioResponse(
                servicio.getIdServicio(),
                servicio.getNombre(),
                servicio.getDescripcion(),
                servicio.getPrecio(),
                servicio.getDuracionMinutos(),
                servicio.getEstado()
        );
    }
}