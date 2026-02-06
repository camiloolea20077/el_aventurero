package com.cloud_technological.el_aventurero.services;

import java.util.List;

import com.cloud_technological.el_aventurero.dto.roles.CreateRoleDto;
import com.cloud_technological.el_aventurero.dto.roles.RoleDto;
import com.cloud_technological.el_aventurero.dto.roles.UpdateRoleDto;

public interface RoleService {
    RoleDto create(CreateRoleDto createDto);
    Boolean update(UpdateRoleDto updateDto);
    Boolean delete(Long id);
    RoleDto findById(Long id);
    List<RoleDto> findAll();
    List<RoleDto> findAllActive();
}
