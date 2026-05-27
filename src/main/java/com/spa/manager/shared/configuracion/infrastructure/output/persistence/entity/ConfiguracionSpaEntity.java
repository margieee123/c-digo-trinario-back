    package com.spa.manager.shared.configuracion.infrastructure.output.persistence.entity;

    import jakarta.persistence.*;

    @Entity
    @Table(name = "configuracion_spa")
    public class ConfiguracionSpaEntity {

        @Id
        @Column(name = "id")
        private Integer id = 1;

        @Column(nullable = false)
        private String nombre;

        private String direccion;
        private String telefono;
        private String email;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public String getDireccion() { return direccion; }
        public void setDireccion(String direccion) { this.direccion = direccion; }
        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }