package com.cloud_technological.el_aventurero.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ajuste_inventario")
@Getter
@Setter
public class AjusteInventarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long producto_id;

    @Column(nullable = false, length = 20)
    private String tipo; // SUMA, RESTA

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, length = 100)
    private String motivo; // MERMA, ROBO, ERROR_CONTEO, VENTA_NO_REGISTRADA, OTRO

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "conteo_id")
    private Long conteo_id;

    @Column(name = "usuario_id")
    private Long usuario_id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
    }
}
