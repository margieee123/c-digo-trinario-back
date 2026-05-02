package com.spa.manager.usuarioadmin.application.service;

import com.spa.manager.auth.application.ports.output.PasswordEncoderPort;
import com.spa.manager.auth.application.ports.output.UsuarioRepositoryPort;
import com.spa.manager.auth.domain.exception.CorreoYaRegistradoException;
import com.spa.manager.auth.domain.model.Estado;
import com.spa.manager.auth.domain.model.Rol;
import com.spa.manager.auth.domain.model.Usuario;
import com.spa.manager.auth.infrastructure.output.persistence.repository.UsuarioJpaRepository;
import com.spa.manager.reservas.application.ports.output.ReservaRepositoryPort;
import com.spa.manager.servicios.application.ports.output.ServicioRepositoryPort;
import com.spa.manager.usuarioadmin.application.dto.*;
import com.spa.manager.usuarioadmin.application.ports.input.*;
import com.spa.manager.usuarioadmin.application.ports.output.DisponibilidadRepositoryPort;
import com.spa.manager.usuarioadmin.application.ports.output.TerapeutaServicioRepositoryPort;
import com.spa.manager.usuarioadmin.domain.model.Disponibilidad;
import com.spa.manager.usuarioadmin.domain.model.TerapeutaServicio;
import com.spa.manager.usuarios.application.dto.UsuarioResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioAdminService implements CrearStaffUseCase, GestionarDisponibilidadUseCase,
        GestionarTerapeutaServicioUseCase, ConsultarTerapeutasDisponiblesUseCase,
        VerAgendaTerapeutaUseCase, CambiarRolUsuarioUseCase, ListarStaffUseCase {

    private final UsuarioRepositoryPort usuarioRepository;
    private final PasswordEncoderPort passwordEncoder;
    private final DisponibilidadRepositoryPort disponibilidadRepository;
    private final TerapeutaServicioRepositoryPort terapeutaServicioRepository;
    private final ReservaRepositoryPort reservaRepository;
    private final ServicioRepositoryPort servicioRepository;
    private final UsuarioJpaRepository usuarioJpaRepository;

    public UsuarioAdminService(UsuarioRepositoryPort usuarioRepository,
                               PasswordEncoderPort passwordEncoder,
                               DisponibilidadRepositoryPort disponibilidadRepository,
                               TerapeutaServicioRepositoryPort terapeutaServicioRepository,
                               ReservaRepositoryPort reservaRepository,
                               ServicioRepositoryPort servicioRepository,
                               UsuarioJpaRepository usuarioJpaRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.disponibilidadRepository = disponibilidadRepository;
        this.terapeutaServicioRepository = terapeutaServicioRepository;
        this.reservaRepository = reservaRepository;
        this.servicioRepository = servicioRepository;
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    // ─── CREAR STAFF ──────────────────────────────────────────────────────────

    @Override
    public UsuarioResponse crearStaff(CrearStaffRequest request) {
        if (usuarioRepository.existsBycorreo(request.getCorreo())) {
            throw new CorreoYaRegistradoException("El correo ya está registrado.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setPasswordhash(passwordEncoder.encode(request.getPassword()));
        usuario.setRol(request.getRol());
        usuario.setEstado(Estado.activo);

        Usuario guardado = usuarioRepository.save(usuario);
        return toUsuarioResponse(guardado);
    }

    // ─── DISPONIBILIDAD ───────────────────────────────────────────────────────

    @Override
    public DisponibilidadResponse agregar(Integer idTerapeuta, DisponibilidadRequest request) {
        Disponibilidad d = new Disponibilidad();
        d.setIdTerapeuta(idTerapeuta);
        d.setDiaSemana(request.getDiaSemana());
        d.setHoraInicio(request.getHoraInicio());
        d.setHoraFin(request.getHoraFin());

        Disponibilidad guardada = disponibilidadRepository.save(d);
        return toDisponibilidadResponse(guardada);
    }

    @Override
    public List<DisponibilidadResponse> listarPorTerapeuta(Integer idTerapeuta) {
        return disponibilidadRepository.findByIdTerapeuta(idTerapeuta).stream()
                .map(this::toDisponibilidadResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(Integer idDisponibilidad) {
        disponibilidadRepository.deleteById(idDisponibilidad);
    }

    // ─── TERAPEUTA-SERVICIO ───────────────────────────────────────────────────

    @Override
    public void asignarServicio(Integer idTerapeuta, Integer idServicio) {
        if (terapeutaServicioRepository.exists(idTerapeuta, idServicio)) {
            throw new IllegalStateException("El terapeuta ya tiene asignado ese servicio.");
        }
        terapeutaServicioRepository.save(new TerapeutaServicio(idTerapeuta, idServicio));
    }

    @Override
    public void removerServicio(Integer idTerapeuta, Integer idServicio) {
        terapeutaServicioRepository.delete(idTerapeuta, idServicio);
    }

    @Override
    public List<Integer> listarServiciosDeTerapeuta(Integer idTerapeuta) {
        return terapeutaServicioRepository.findServiciosByIdTerapeuta(idTerapeuta);
    }

    @Override
    public List<Integer> listarTerapeutasDeServicio(Integer idServicio) {
        return terapeutaServicioRepository.findTerapeutasByIdServicio(idServicio);
    }

    // ─── TERAPEUTAS DISPONIBLES ───────────────────────────────────────────────

    @Override
    public List<TerapeutaDisponibleResponse> consultar(Integer idServicio, LocalDate fecha) {
        List<Integer> terapeutas = terapeutaServicioRepository.findTerapeutasByIdServicio(idServicio);

        String diaSemana = switch (fecha.getDayOfWeek()) {
            case MONDAY -> "lunes";
            case TUESDAY -> "martes";
            case WEDNESDAY -> "miercoles";
            case THURSDAY -> "jueves";
            case FRIDAY -> "viernes";
            case SATURDAY -> "sabado";
            case SUNDAY -> "domingo";
        };

        return terapeutas.stream().map(idTerapeuta -> {
                    TerapeutaDisponibleResponse r = new TerapeutaDisponibleResponse();
                    r.setIdTerapeuta(idTerapeuta);

                    usuarioJpaRepository.findById(idTerapeuta)
                            .ifPresent(u -> r.setNombre(u.getNombre()));

                    List<String> horarios = disponibilidadRepository
                            .findByIdTerapeuta(idTerapeuta).stream()
                            .filter(d -> d.getDiaSemana().name().equalsIgnoreCase(diaSemana))
                            .map(d -> d.getHoraInicio() + " - " + d.getHoraFin())
                            .collect(Collectors.toList());

                    r.setHorariosDisponibles(horarios);
                    return r;
                }).filter(r -> !r.getHorariosDisponibles().isEmpty())
                .collect(Collectors.toList());
    }

    // ─── AGENDA TERAPEUTA ─────────────────────────────────────────────────────

    @Override
    public List<AgendaResponse> verAgenda(Integer idTerapeuta, LocalDate fecha) {
        return reservaRepository.findByIdTerapeuta(idTerapeuta).stream()
                .filter(r -> r.getFecha().equals(fecha))
                .map(r -> {
                    AgendaResponse agenda = new AgendaResponse();
                    agenda.setIdReserva(r.getIdReserva());
                    agenda.setFecha(r.getFecha());
                    agenda.setHoraInicio(r.getHoraInicio());
                    agenda.setHoraFin(r.getHoraFin());
                    agenda.setEstado(r.getEstado());

                    usuarioJpaRepository.findById(r.getIdCliente())
                            .ifPresent(u -> agenda.setNombreCliente(u.getNombre()));
                    servicioRepository.findById(r.getIdServicio())
                            .ifPresent(s -> agenda.setNombreServicio(s.getNombre()));

                    return agenda;
                }).collect(Collectors.toList());
    }

    // ─── CAMBIAR ROL ──────────────────────────────────────────────────────────

    @Override
    public UsuarioResponse cambiarRol(Integer idUsuario, Rol nuevoRol) {
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado."));
        usuario.setRol(nuevoRol);
        Usuario actualizado = usuarioRepository.save(usuario);
        return toUsuarioResponse(actualizado);
    }

    // ─── LISTAR STAFF ─────────────────────────────────────────────────────────

    @Override
    public List<UsuarioResponse> listarTerapeutas() {
        return usuarioJpaRepository.findByRol(Rol.terapeuta).stream()
                .map(u -> new UsuarioResponse(u.getId(), u.getNombre(),
                        u.getCorreo(), u.getRol(), u.getEstado()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UsuarioResponse> listarRecepcionistas() {
        return usuarioJpaRepository.findByRol(Rol.recepcionista).stream()
                .map(u -> new UsuarioResponse(u.getId(), u.getNombre(),
                        u.getCorreo(), u.getRol(), u.getEstado()))
                .collect(Collectors.toList());
    }

    // ─── MAPPERS ──────────────────────────────────────────────────────────────

    private UsuarioResponse toUsuarioResponse(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(),
                u.getCorreo(), u.getRol(), u.getEstado());
    }

    private DisponibilidadResponse toDisponibilidadResponse(Disponibilidad d) {
        DisponibilidadResponse r = new DisponibilidadResponse();
        r.setIdDisponibilidad(d.getIdDisponibilidad());
        r.setIdTerapeuta(d.getIdTerapeuta());
        r.setDiaSemana(d.getDiaSemana());
        r.setHoraInicio(d.getHoraInicio());
        r.setHoraFin(d.getHoraFin());

        usuarioJpaRepository.findById(d.getIdTerapeuta())
                .ifPresent(u -> r.setNombreTerapeuta(u.getNombre()));

        return r;
    }
}