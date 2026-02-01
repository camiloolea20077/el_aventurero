package com.cloud_technological.el_aventurero.repositories.detalle_compras;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud_technological.el_aventurero.entity.DetalleCompraEntity;

public interface DetalleCompraJPARepository extends JpaRepository<DetalleCompraEntity, Long> {
    // Solo operaciones CRUD básicas
}