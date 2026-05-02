package com.spa.manager.usuarioadmin.infrastructure.output.persistence.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "Terapeuta_Servicio")
@IdClass(TerapeutaServicioEntity.TerapeutaServicioId.class)
public class TerapeutaServicioEntity {

    @Id
    @Column(name = "id_terapeuta")
    private Integer idTerapeuta;

    @Id
    @Column(name = "id_servicio")
    private Integer idServicio;

    public Integer getIdTerapeuta() { return idTerapeuta; }
    public void setIdTerapeuta(Integer idTerapeuta) { this.idTerapeuta = idTerapeuta; }

    public Integer getIdServicio() { return idServicio; }
    public void setIdServicio(Integer idServicio) { this.idServicio = idServicio; }

    public static class TerapeutaServicioId implements Serializable {
        private Integer idTerapeuta;
        private Integer idServicio;

        public TerapeutaServicioId() {}

        public TerapeutaServicioId(Integer idTerapeuta, Integer idServicio) {
            this.idTerapeuta = idTerapeuta;
            this.idServicio = idServicio;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TerapeutaServicioId)) return false;
            TerapeutaServicioId that = (TerapeutaServicioId) o;
            return Objects.equals(idTerapeuta, that.idTerapeuta)
                    && Objects.equals(idServicio, that.idServicio);
        }

        @Override
        public int hashCode() {
            return Objects.hash(idTerapeuta, idServicio);
        }
    }
}