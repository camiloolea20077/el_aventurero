package com.cloud_technological.el_aventurero.entity;

import java.math.BigDecimal;
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
@Table(name = "detalle_compra")
@Getter
@Setter
public class DetalleCompraEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long compra_id;

    @Column(nullable = false)
    private Long producto_id;

    @Column(nullable = false)
    private Integer cajas;

    @Column(nullable = false)
    private Integer unidades_por_caja;

    @Column(nullable = false)
    private Integer total_unidades;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costo_total;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal costo_unitario;

    @Column(precision = 12, scale = 2)
    private BigDecimal precio_sugerido;

    @Column(precision = 12, scale = 2)
    private BigDecimal precio_venta;

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
        if (cajas == null) {
            cajas = 0;
        }
        if (unidades_por_caja == null) {
            unidades_por_caja = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updated_at = LocalDateTime.now();
    }
}