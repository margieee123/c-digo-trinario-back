package com.spa.manager.auth.infrastructure.input.rest;

import com.spa.manager.auth.application.dto.AuthResponse;
import com.spa.manager.auth.application.dto.LoginCommand;
import com.spa.manager.auth.application.dto.RegisterCommand;
import com.spa.manager.auth.application.ports.input.LoginUseCase;
import com.spa.manager.auth.application.ports.input.RegisterUseCase;
import com.spa.manager.auth.infrastructure.input.rest.dto.LoginRequest;
import com.spa.manager.auth.infrastructure.input.rest.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final RegisterUseCase registerUseCase;

    public AuthController(LoginUseCase loginUseCase, RegisterUseCase registerUseCase) {
        this.loginUseCase = loginUseCase;
        this.registerUseCase = registerUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(request.getCorreo(), request.getPassword());
        AuthResponse response = loginUseCase.login(command);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterCommand command = new RegisterCommand(request.getNombre(), request.getCorreo(), request.getPassword());
        AuthResponse response = registerUseCase.register(command);
        return ResponseEntity.status(201).body(response);
    }
}