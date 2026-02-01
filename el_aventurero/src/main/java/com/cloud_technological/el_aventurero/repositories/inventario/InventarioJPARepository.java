package com.cloud_technological.el_aventurero.repositories.inventario;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud_technological.el_aventurero.entity.InventarioEntity;

public interface InventarioJPARepository extends JpaRepository<InventarioEntity, Long> {
    // Solo operaciones CRUD básicas
}