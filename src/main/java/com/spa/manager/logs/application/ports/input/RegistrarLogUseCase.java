package com.spa.manager.logs.application.ports.input;
import com.spa.manager.logs.domain.model.Log;

public interface RegistrarLogUseCase {
    void registrar(Log log);
}