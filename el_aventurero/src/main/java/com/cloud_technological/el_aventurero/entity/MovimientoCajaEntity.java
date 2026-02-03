package com.cloud_technological.el_aventurero.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "movimiento_caja")
@Getter
@Setter
public class MovimientoCajaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String tipo; // INGRESO, EGRESO

    @Column(nullable = false, length = 100)
    private String concepto;

    @Column(nullable = false, length = 50)
    private String categoria; // VENTA, COMPRA, SALARIO, SERVICIO, GASTO, OTRO

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(length = 50)
    private String metodo_pago; // EFECTIVO, TARJETA, TRANSFERENCIA, DIGITAL

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "venta_id")
    private Long venta_id;

    @Column(name = "compra_id")
    private Long compra_id;

    @Column(nullable = false)
    private Long activo;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
        if (activo == null) {
            activo = 1L;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updated_at = LocalDateTime.now();
    }
}