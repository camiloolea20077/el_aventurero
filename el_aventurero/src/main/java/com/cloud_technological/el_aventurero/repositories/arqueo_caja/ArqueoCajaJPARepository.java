package com.cloud_technological.el_aventurero.repositories.arqueo_caja;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud_technological.el_aventurero.entity.ArqueoCajaEntity;

public interface ArqueoCajaJPARepository extends JpaRepository<ArqueoCajaEntity, Long> {
    // Solo operaciones CRUD básicas
}