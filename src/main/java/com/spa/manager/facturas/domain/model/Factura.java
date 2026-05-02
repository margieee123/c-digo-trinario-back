package com.spa.manager.facturas.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Factura {

    private Integer idFactura;
    private Integer idReserva;
    private BigDecimal monto;
    private LocalDate fechaEmision;
    private EstadoPago estadoPago;

    public Factura() {}

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
}