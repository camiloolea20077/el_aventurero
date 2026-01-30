package com.cloud_technological.el_aventurero.repositories.users;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cloud_technological.el_aventurero.entity.UserEntity;


public interface UserJPARepository extends JpaRepository<UserEntity, Long>  {
    public Optional<UserEntity> findByEmail(String email);
}
