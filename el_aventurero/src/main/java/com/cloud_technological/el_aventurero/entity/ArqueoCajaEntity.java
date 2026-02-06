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
@Table(name = "arqueo_caja")
@Getter
@Setter
public class ArqueoCajaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo_inicial;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total_ingresos_sistema;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal total_egresos_sistema;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo_esperado;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal efectivo_real;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal diferencia;

    @Column(nullable = false, length = 20)
    private String estado; // PENDIENTE, CUADRADO, AJUSTADO

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    // Detalle del conteo de billetes
    @Column(nullable = false)
    private Integer billetes_100000;

    @Column(nullable = false)
    private Integer billetes_50000;

    @Column(nullable = false)
    private Integer billetes_20000;

    @Column(nullable = false)
    private Integer billetes_10000;

    @Column(nullable = false)
    private Integer billetes_5000;

    @Column(nullable = false)
    private Integer billetes_2000;

    @Column(nullable = false)
    private Integer billetes_1000;

    // Detalle del conteo de monedas
    @Column(nullable = false)
    private Integer monedas_1000;

    @Column(nullable = false)
    private Integer monedas_500;

    @Column(nullable = false)
    private Integer monedas_200;

    @Column(nullable = false)
    private Integer monedas_100;

    @Column(nullable = false)
    private Integer monedas_50;

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