package com.spa.manager.servicios.application.ports.input;

import com.spa.manager.servicios.application.dto.ServicioResponse;
import java.util.List;

public interface ListarServiciosUseCase {
    List<ServicioResponse> listar();
    List<ServicioResponse> buscarPorNombre(String nombre);
}