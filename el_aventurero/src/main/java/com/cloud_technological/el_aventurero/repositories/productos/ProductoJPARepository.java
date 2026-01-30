package com.cloud_technological.el_aventurero.repositories.productos;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cloud_technological.el_aventurero.entity.ProductoEntity;

public interface ProductoJPARepository extends JpaRepository<ProductoEntity, Long> {
    Optional<ProductoEntity> findByNombre(String nombre);

}