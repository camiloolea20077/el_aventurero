package com.cloud_technological.el_aventurero.repositories.consumo_mesa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud_technological.el_aventurero.entity.ConsumoMesaEntity;

public interface ConsumoMesaJPARepository extends JpaRepository<ConsumoMesaEntity, Long> {
    // Solo operaciones CRUD básicas
}