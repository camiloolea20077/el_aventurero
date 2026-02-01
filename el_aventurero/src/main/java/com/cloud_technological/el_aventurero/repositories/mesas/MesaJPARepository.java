package com.cloud_technological.el_aventurero.repositories.mesas;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud_technological.el_aventurero.entity.MesaEntity;

public interface MesaJPARepository extends JpaRepository<MesaEntity, Long> {
    // Solo operaciones CRUD básicas
}