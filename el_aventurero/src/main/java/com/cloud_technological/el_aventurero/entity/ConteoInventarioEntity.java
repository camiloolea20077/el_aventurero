package com.cloud_technological.el_aventurero.entity;

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
@Table(name = "conteo_inventario")
@Getter
@Setter
public class ConteoInventarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(length = 20)
    private String tipo; // PERIODICO, CICLICO, ANUAL

    @Column(length = 20)
    private String estado; // EN_PROCESO, COMPLETADO

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    @Column(name = "updated_at")
    private LocalDateTime updated_at;

    @Column(name = "deleted_at")
    private LocalDateTime deleted_at;

    @PrePersist
    protected void onCreate() {
        created_at = LocalDateTime.now();
        if (tipo == null || tipo.isBlank()) {
            tipo = "PERIODICO";
        }
        if (estado == null || estado.isBlank()) {
            estado = "EN_PROCESO";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updated_at = LocalDateTime.now();
    }
}
