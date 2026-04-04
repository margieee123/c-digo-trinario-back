package com.spa.manager.logs.domain.model;

import java.time.LocalDateTime;

public class Log {

    private Integer idLog;
    private TipoLog tipo;
    private Integer idUsuario;
    private String descripcion;
    private String ip;
    private LocalDateTime fechaHora;

    public Log() {}

    public Log(TipoLog tipo, Integer idUsuario, String descripcion, String ip) {
        this.tipo = tipo;
        this.idUsuario = idUsuario;
        this.descripcion = descripcion;
        this.ip = ip;
        this.fechaHora = LocalDateTime.now();
    }

    public Integer getIdLog() { return idLog; }
    public void setIdLog(Integer idLog) { this.idLog = idLog; }

    public TipoLog getTipo() { return tipo; }
    public void setTipo(TipoLog tipo) { this.tipo = tipo; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}