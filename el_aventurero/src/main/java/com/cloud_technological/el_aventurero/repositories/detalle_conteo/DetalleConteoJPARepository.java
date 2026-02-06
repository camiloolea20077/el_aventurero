package com.cloud_technological.el_aventurero.repositories.detalle_conteo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud_technological.el_aventurero.entity.DetalleConteoEntity;

public interface DetalleConteoJPARepository extends JpaRepository<DetalleConteoEntity, Long> {
}