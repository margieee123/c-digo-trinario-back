package com.spa.manager.mappers;

import com.spa.manager.calificaciones.domain.model.Calificacion;
import com.spa.manager.calificaciones.infrastructure.output.persistence.entity.CalificacionEntity;
import com.spa.manager.calificaciones.infrastructure.output.persistence.mapper.CalificacionMapper;
import com.spa.manager.usuarioadmin.domain.model.DiaSemana;
import com.spa.manager.usuarioadmin.domain.model.Disponibilidad;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.entity.DisponibilidadEntity;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.mapper.DisponibilidadMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import com.spa.manager.usuarioadmin.domain.model.TerapeutaServicio;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.entity.TerapeutaServicioEntity;
import com.spa.manager.usuarioadmin.infrastructure.output.persistence.mapper.TerapeutaServicioMapper;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas unitarias - Mappers Coverage")
class MappersCoverageTest {

    private final CalificacionMapper calificacionMapper = new CalificacionMapper();
    private final DisponibilidadMapper disponibilidadMapper = new DisponibilidadMapper();

    @Test
    @DisplayName("CalificacionMapper toDomain debe mapear correctamente")
    void calificacionMapper_toDomain_debeMapearCorrectamente() {
        CalificacionEntity entity = new CalificacionEntity();
        entity.setIdCalificacion(1);
        entity.setIdReserva(2);
        entity.setPuntuacion(5);
        entity.setComentario("Excelente");
        entity.setFecha(LocalDate.of(2026, 6, 1));

        Calificacion domain = calificacionMapper.toDomain(entity);

        assertEquals(1, domain.getIdCalificacion());
        assertEquals(2, domain.getIdReserva());
        assertEquals(5, domain.getPuntuacion());
        assertEquals("Excelente", domain.getComentario());
        assertEquals(LocalDate.of(2026, 6, 1), domain.getFecha());
    }

    @Test
    @DisplayName("CalificacionMapper toEntity debe mapear correctamente")
    void calificacionMapper_toEntity_debeMapearCorrectamente() {
        Calificacion domain = new Calificacion();
        domain.setIdCalificacion(1);
        domain.setIdReserva(2);
        domain.setPuntuacion(4);
        domain.setComentario("Muy bueno");
        domain.setFecha(LocalDate.of(2026, 6, 1));

        CalificacionEntity entity = calificacionMapper.toEntity(domain);

        assertEquals(1, entity.getIdCalificacion());
        assertEquals(2, entity.getIdReserva());
        assertEquals(4, entity.getPuntuacion());
        assertEquals("Muy bueno", entity.getComentario());
        assertEquals(LocalDate.of(2026, 6, 1), entity.getFecha());
    }

    @Test
    @DisplayName("DisponibilidadMapper toDomain debe mapear correctamente")
    void disponibilidadMapper_toDomain_debeMapearCorrectamente() {
        DisponibilidadEntity entity = new DisponibilidadEntity();
        entity.setIdDisponibilidad(1);
        entity.setIdTerapeuta(2);
        entity.setDiaSemana(DiaSemana.lunes);
        entity.setHoraInicio(LocalTime.of(8, 0));
        entity.setHoraFin(LocalTime.of(17, 0));

        Disponibilidad domain = disponibilidadMapper.toDomain(entity);

        assertEquals(1, domain.getIdDisponibilidad());
        assertEquals(2, domain.getIdTerapeuta());
        assertEquals(DiaSemana.lunes, domain.getDiaSemana());
        assertEquals(LocalTime.of(8, 0), domain.getHoraInicio());
        assertEquals(LocalTime.of(17, 0), domain.getHoraFin());
    }

    @Test
    @DisplayName("DisponibilidadMapper toEntity debe mapear correctamente")
    void disponibilidadMapper_toEntity_debeMapearCorrectamente() {
        Disponibilidad domain = new Disponibilidad();
        domain.setIdDisponibilidad(1);
        domain.setIdTerapeuta(2);
        domain.setDiaSemana(DiaSemana.martes);
        domain.setHoraInicio(LocalTime.of(9, 0));
        domain.setHoraFin(LocalTime.of(18, 0));

        DisponibilidadEntity entity = disponibilidadMapper.toEntity(domain);

        assertEquals(1, entity.getIdDisponibilidad());
        assertEquals(2, entity.getIdTerapeuta());
        assertEquals(DiaSemana.martes, entity.getDiaSemana());
        assertEquals(LocalTime.of(9, 0), entity.getHoraInicio());
        assertEquals(LocalTime.of(18, 0), entity.getHoraFin());
    }

    @Test
    @DisplayName("CalificacionEntity debe setear y obtener campos correctamente")
    void calificacionEntity_debeSetearYObtenerCampos() {
        CalificacionEntity entity = new CalificacionEntity();
        entity.setIdCalificacion(1);
        entity.setIdReserva(2);
        entity.setPuntuacion(5);
        entity.setComentario("Perfecto");
        entity.setFecha(LocalDate.of(2026, 6, 1));

        assertEquals(1, entity.getIdCalificacion());
        assertEquals(2, entity.getIdReserva());
        assertEquals(5, entity.getPuntuacion());
        assertEquals("Perfecto", entity.getComentario());
        assertEquals(LocalDate.of(2026, 6, 1), entity.getFecha());
    }

    @Test
    @DisplayName("DisponibilidadEntity debe setear y obtener campos correctamente")
    void disponibilidadEntity_debeSetearYObtenerCampos() {
        DisponibilidadEntity entity = new DisponibilidadEntity();
        entity.setIdDisponibilidad(1);
        entity.setIdTerapeuta(2);
        entity.setDiaSemana(DiaSemana.miercoles);
        entity.setHoraInicio(LocalTime.of(8, 0));
        entity.setHoraFin(LocalTime.of(16, 0));

        assertEquals(1, entity.getIdDisponibilidad());
        assertEquals(2, entity.getIdTerapeuta());
        assertEquals(DiaSemana.miercoles, entity.getDiaSemana());
        assertEquals(LocalTime.of(8, 0), entity.getHoraInicio());
        assertEquals(LocalTime.of(16, 0), entity.getHoraFin());
    }
    private final TerapeutaServicioMapper terapeutaServicioMapper = new TerapeutaServicioMapper();

    @Test
    @DisplayName("TerapeutaServicioMapper toDomain debe mapear correctamente")
    void terapeutaServicioMapper_toDomain_debeMapearCorrectamente() {
        TerapeutaServicioEntity entity = new TerapeutaServicioEntity();
        entity.setIdTerapeuta(1);
        entity.setIdServicio(2);

        TerapeutaServicio domain = terapeutaServicioMapper.toDomain(entity);

        assertEquals(1, domain.getIdTerapeuta());
        assertEquals(2, domain.getIdServicio());
    }

    @Test
    @DisplayName("TerapeutaServicioMapper toEntity debe mapear correctamente")
    void terapeutaServicioMapper_toEntity_debeMapearCorrectamente() {
        TerapeutaServicio domain = new TerapeutaServicio(1, 2);

        TerapeutaServicioEntity entity = terapeutaServicioMapper.toEntity(domain);

        assertEquals(1, entity.getIdTerapeuta());
        assertEquals(2, entity.getIdServicio());
    }
}