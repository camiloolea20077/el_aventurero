package com.cloud_technological.el_aventurero.repositories.compras;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud_technological.el_aventurero.entity.CompraEntity;

public interface CompraJPARepository extends JpaRepository<CompraEntity, Long> {
    // Solo operaciones CRUD básicas
}