package com.spa.manager.reservas.application.dto;

import java.util.List;
import java.util.Map;

public class DisponibilidadSemanaResponse {
    // Clave: "2026-05-20T09:00" → valor: true=disponible, false=no disponible
    private Map<String, Boolean> slots;

    public DisponibilidadSemanaResponse(Map<String, Boolean> slots) {
        this.slots = slots;
    }

    public Map<String, Boolean> getSlots() { return slots; }
    public void setSlots(Map<String, Boolean> slots) { this.slots = slots; }
}