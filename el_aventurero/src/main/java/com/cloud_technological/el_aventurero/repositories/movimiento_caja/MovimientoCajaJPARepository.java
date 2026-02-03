package com.cloud_technological.el_aventurero.repositories.movimiento_caja;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud_technological.el_aventurero.entity.MovimientoCajaEntity;

public interface MovimientoCajaJPARepository extends JpaRepository<MovimientoCajaEntity, Long> {
    // Solo operaciones CRUD básicas
}