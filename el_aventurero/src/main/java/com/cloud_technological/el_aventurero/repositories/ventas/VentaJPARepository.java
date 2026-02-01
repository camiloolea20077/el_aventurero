package com.cloud_technological.el_aventurero.repositories.ventas;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud_technological.el_aventurero.entity.VentaEntity;

public interface VentaJPARepository extends JpaRepository<VentaEntity, Long> {
    // Solo operaciones CRUD básicas
}