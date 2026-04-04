package com.spa.manager.auth.application.ports.input;

import com.spa.manager.auth.application.dto.AuthResponse;
import com.spa.manager.auth.application.dto.LoginCommand;

public interface LoginUseCase {
    AuthResponse login(LoginCommand command);
}
