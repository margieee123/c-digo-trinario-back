package com.spa.manager.usuarios.application.service;

import com.spa.manager.auth.domain.model.Estado;
import com.spa.manager.auth.domain.model.Usuario;
import com.spa.manager.auth.domain.exception.UsuarioNoEncontradoException;
import com.spa.manager.auth.application.ports.output.UsuarioRepositoryPort;
import com.spa.manager.shared.email.EmailRequest;
import com.spa.manager.shared.email.EmailService;
import com.spa.manager.usuarios.application.dto.UsuarioRequest;
import com.spa.manager.usuarios.application.dto.UsuarioResponse;
import com.spa.manager.usuarios.application.ports.input.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService implements
        ListarUsuariosUseCase,
        ObtenerUsuarioUseCase,
        CambiarEstadoUsuarioUseCase,
        ObtenerPerfilUseCase,
        CrearUsuarioUseCase,
        ActualizarUsuarioUseCase,
        EliminarUsuarioUseCase,
        CambiarPasswordUseCase,
        RecuperarPasswordUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UsuarioService(UsuarioRepositoryPort usuarioRepositoryPort,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public List<UsuarioResponse> listar() {
        return usuarioRepositoryPort.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioResponse> buscarPorNombre(String nombre) {
        return usuarioRepositoryPort.findAll()
                .stream()
                .filter(u -> u.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(u -> u.getRol().name().equals("cliente"))
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponse obtener(Integer id) {
        Usuario usuario = usuarioRepositoryPort.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con id: " + id));
        return toResponse(usuario);
    }

    @Override
    public UsuarioResponse cambiarEstado(Integer id, Estado estado) {
        Usuario usuario = usuarioRepositoryPort.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con id: " + id));
        usuario.setEstado(estado);
        return toResponse(usuarioRepositoryPort.save(usuario));
    }

    @Override
    public UsuarioResponse obtenerPerfil(String correo) {
        Usuario usuario = usuarioRepositoryPort.findBycorreo(correo)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        return toResponse(usuario);
    }

    @Override
    public UsuarioResponse crear(UsuarioRequest request) {
        if (usuarioRepositoryPort.existsBycorreo(request.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setPasswordhash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        usuario.setEstado(Estado.activo);
        return toResponse(usuarioRepositoryPort.save(usuario));
    }

    @Override
    public UsuarioResponse actualizar(Integer id, UsuarioRequest request) {
        Usuario usuario = usuarioRepositoryPort.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con id: " + id));
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPasswordhash(passwordEncoder.encode(request.getPassword()));
        }
        usuario.setRol(request.getRol());
        return toResponse(usuarioRepositoryPort.save(usuario));
    }

    @Override
    public void eliminar(Integer id) {
        Usuario usuario = usuarioRepositoryPort.findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con id: " + id));
        usuario.setEstado(Estado.inactivo);
        usuarioRepositoryPort.save(usuario);
    }

    @Override
    public void cambiarPassword(String correo, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepositoryPort.findBycorreo(correo)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));

        if (!passwordEncoder.matches(passwordActual, usuario.getPasswordhash())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta.");
        }

        if (passwordNueva == null || passwordNueva.length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres.");
        }

        usuario.setPasswordhash(passwordEncoder.encode(passwordNueva));
        usuarioRepositoryPort.save(usuario);
    }

    @Override
    public void recuperarPassword(String correo) {
        Usuario usuario = usuarioRepositoryPort.findBycorreo(correo)
                .orElseThrow(() -> new UsuarioNoEncontradoException("No existe un usuario con ese correo."));

        String passwordTemporal = generarPasswordTemporal();
        usuario.setPasswordhash(passwordEncoder.encode(passwordTemporal));
        usuarioRepositoryPort.save(usuario);

        emailService.enviar(new EmailRequest(
                correo,
                "Recuperación de contraseña - Spa Manager",
                buildEmailRecuperacion(usuario.getNombre(), passwordTemporal)
        ));
    }

    private String generarPasswordTemporal() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String buildEmailRecuperacion(String nombre, String passwordTemporal) {
        return """
            <h2>¡Hola %s!</h2>
            <p>Recibimos una solicitud para restablecer tu contraseña.</p>
            <p>Tu contraseña temporal es:</p>
            <h3 style="background:#f5f5f5;padding:12px;border-radius:6px;font-family:monospace;">%s</h3>
            <p>Por seguridad, te recomendamos cambiarla una vez que inicies sesión.</p>
            <p>Si no solicitaste este cambio, ignora este correo.</p>
            <p>Spa Manager</p>
            """.formatted(nombre, passwordTemporal);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol(),
                usuario.getEstado()
        );
    }
}