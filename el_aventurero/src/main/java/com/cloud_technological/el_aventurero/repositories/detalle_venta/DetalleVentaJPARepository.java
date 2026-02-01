package com.cloud_technological.el_aventurero.repositories.detalle_venta;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud_technological.el_aventurero.entity.DetalleVentaEntity;

public interface DetalleVentaJPARepository extends JpaRepository<DetalleVentaEntity, Long> {
    // Solo operaciones CRUD básicas
}