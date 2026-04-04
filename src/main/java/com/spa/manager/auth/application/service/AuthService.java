package com.spa.manager.auth.application.service;

import com.spa.manager.auth.application.dto.AuthResponse;
import com.spa.manager.auth.application.dto.LoginCommand;
import com.spa.manager.auth.application.dto.RegisterCommand;
import com.spa.manager.auth.application.ports.input.LoginUseCase;
import com.spa.manager.auth.application.ports.input.RegisterUseCase;
import com.spa.manager.auth.application.ports.output.JwtServicePort;
import com.spa.manager.auth.application.ports.output.PasswordEncoderPort;
import com.spa.manager.auth.application.ports.output.UsuarioRepositoryPort;
import com.spa.manager.auth.domain.exception.CorreoYaRegistradoException;
import com.spa.manager.auth.domain.exception.CredencialesInvalidasException;
import com.spa.manager.auth.domain.exception.UsuarioInactivoException;
import com.spa.manager.auth.domain.exception.UsuarioNoEncontradoException;
import com.spa.manager.auth.domain.model.Estado;
import com.spa.manager.auth.domain.model.Rol;
import com.spa.manager.auth.domain.model.Usuario;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements LoginUseCase, RegisterUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final JwtServicePort jwtService;

    public AuthService(UsuarioRepositoryPort usuarioRepository,
                       PasswordEncoderPort passwordEncoder,
                       JwtServicePort jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse login(LoginCommand command) {
        Usuario usuario = usuarioRepository.findBycorreo(command.getCorreo())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado."));

        if (!usuario.estaActivo()) {
            throw new UsuarioInactivoException("El usuario está inactivo.");
        }

        if (!passwordEncoder.matches(command.getPassword(), usuario.getPasswordhash())) {
            throw new CredencialesInvalidasException("Correo o contraseña incorrectos.");
        }

        String token = jwtService.generateToken(usuario);
        return new AuthResponse(token, usuario.getId(), usuario.getNombre(),
                usuario.getCorreo(),
                 usuario.getRol());
    }

    @Override
    public AuthResponse register(RegisterCommand command) {
        if (usuarioRepository.existsBycorreo(command.getCorreo())) {
            throw new CorreoYaRegistradoException("El correo ya está registrado.");
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(command.getNombre());
        nuevo.setCorreo(command.getCorreo());
        nuevo.setPasswordhash(passwordEncoder.encode(command.getPassword()));
        nuevo.setRol(Rol.cliente);
        nuevo.setEstado(Estado.activo);

        Usuario guardado = usuarioRepository.save(nuevo);
        String token = jwtService.generateToken(guardado);
        return new AuthResponse(token, guardado.getId(), guardado.getNombre(),
                guardado.getCorreo(), guardado.getRol());
    }
}
