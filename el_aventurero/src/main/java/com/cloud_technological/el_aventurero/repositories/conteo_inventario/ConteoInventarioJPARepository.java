package com.cloud_technological.el_aventurero.repositories.conteo_inventario;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud_technological.el_aventurero.entity.ConteoInventarioEntity;

public interface ConteoInventarioJPARepository extends JpaRepository<ConteoInventarioEntity, Long> {
}