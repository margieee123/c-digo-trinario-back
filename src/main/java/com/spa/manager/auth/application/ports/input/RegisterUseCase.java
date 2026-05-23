package com.spa.manager.auth.application.ports.input;

import com.spa.manager.auth.application.dto.AuthResponse;
import com.spa.manager.auth.application.dto.RegisterCommand;

public interface RegisterUseCase {
    AuthResponse register(RegisterCommand command);
}
