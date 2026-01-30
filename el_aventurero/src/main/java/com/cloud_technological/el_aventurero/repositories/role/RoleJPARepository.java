package com.cloud_technological.el_aventurero.repositories.role;

import java.util.Optional;

import org.springframework.context.annotation.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud_technological.el_aventurero.entity.RoleEntity;



public interface RoleJPARepository extends JpaRepository<RoleEntity, Long> {
    Optional<Role> findByNombre(String nombre);
    public Optional<RoleEntity> existsByNombre(String nombre);
}
