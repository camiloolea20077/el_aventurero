package com.cloud_technological.el_aventurero.entity;

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
@Table(name = "detalle_conteo")
@Getter
@Setter
public class DetalleConteoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long conteo_id;

    @Column(nullable = false)
    private Long producto_id;

    @Column(nullable = false)
    private Integer stock_sistema;

    @Column(nullable = false)
    private Integer stock_fisico;

    @Column(nullable = false)
    private Integer diferencia;

    @Column(columnDefinition = "TEXT")
    private String motivo;

    @Column(nullable = false)
    private Boolean ajustado = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
        if (ajustado == null) {
            ajustado = Boolean.FALSE;
        }
        if (diferencia == null && stock_fisico != null && stock_sistema != null) {
            diferencia = stock_fisico - stock_sistema;
        }
    }
}
