package com.spa.manager.facturas.application.dto;

import com.spa.manager.facturas.domain.model.EstadoPago;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class FacturaResponse {

    private Integer idFactura;
    private Integer idReserva;
    private BigDecimal monto;
    private LocalDate fechaEmision;
    private EstadoPago estadoPago;

    // Datos de la reserva
    private String nombreCliente;
    private String nombreTerapeuta;
    private LocalDate fechaReserva;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private List<String> nombresServicios;
    private String estadoReserva;

    public FacturaResponse() {}

    public Integer getIdFactura() { return idFactura; }
    public void setIdFactura(Integer idFactura) { this.idFactura = idFactura; }

    public Integer getIdReserva() { return idReserva; }
    public void setIdReserva(Integer idReserva) { this.idReserva = idReserva; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public EstadoPago getEstadoPago() { return estadoPago; }
    public void setEstadoPago(EstadoPago estadoPago) { this.estadoPago = estadoPago; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getNombreTerapeuta() { return nombreTerapeuta; }
    public void setNombreTerapeuta(String nombreTerapeuta) { this.nombreTerapeuta = nombreTerapeuta; }

    public LocalDate getFechaReserva() { return fechaReserva; }
    public void setFechaReserva(LocalDate fechaReserva) { this.fechaReserva = fechaReserva; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public List<String> getNombresServicios() { return nombresServicios; }
    public void setNombresServicios(List<String> nombresServicios) { this.nombresServicios = nombresServicios; }

    public String getEstadoReserva() { return estadoReserva; }
    public void setEstadoReserva(String estadoReserva) { this.estadoReserva = estadoReserva; }
}