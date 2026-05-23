package com.spa.manager.logs.application.dto;

import com.spa.manager.logs.domain.model.TipoLog;
import java.time.LocalDateTime;

public class LogResponse {

    private Integer idLog;
    private TipoLog tipo;
    private Integer idUsuario;
    private String nombreUsuario;
    private String descripcion;
    private String ip;
    private LocalDateTime fechaHora;

    public LogResponse() {}

    public LogResponse(Integer idLog, TipoLog tipo, Integer idUsuario,
                       String nombreUsuario, String descripcion,
                       String ip, LocalDateTime fechaHora) {
        this.idLog = idLog;
        this.tipo = tipo;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.descripcion = descripcion;
        this.ip = ip;
        this.fechaHora = fechaHora;
    }

    public Integer getIdLog() { return idLog; }
    public void setIdLog(Integer idLog) { this.idLog = idLog; }

    public TipoLog getTipo() { return tipo; }
    public void setTipo(TipoLog tipo) { this.tipo = tipo; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}