package com.spa.manager.usuarios.application.ports.input;

public interface CambiarPasswordUseCase {
    void cambiarPassword(String correo, String passwordActual, String passwordNueva);
}