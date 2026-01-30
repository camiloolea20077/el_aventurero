package com.cloud_technological.el_aventurero.mappers.roles;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import com.cloud_technological.el_aventurero.dto.roles.CreateRoleDto;
import com.cloud_technological.el_aventurero.dto.roles.RoleDto;
import com.cloud_technological.el_aventurero.dto.roles.UpdateRoleDto;
import com.cloud_technological.el_aventurero.entity.RoleEntity;



@Mapper(componentModel = "spring")
public interface RolesMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "deletedAt", ignore = true),
            @Mapping(target = "activo", source = "dto.active"),
            @Mapping(target = "descripcion", source = "dto.description"),
            @Mapping(target = "nombre", source = "dto.name"),
    })
    RoleEntity createToEntity(CreateRoleDto dto);
    @Mappings({ @Mapping(target = "id", source = "dto.id"), @Mapping(target = "createdAt", ignore = true),
			@Mapping(target = "updatedAt", ignore = true), @Mapping(target = "deletedAt", ignore = true),
			@Mapping(target = "nombre", source = "dto.name"),
			})
	void updateEntityFromDto(UpdateRoleDto dto, @MappingTarget RoleEntity entity);

    @Mappings({
            @Mapping(target = "id", source = "entity.id"),
            @Mapping(target = "name", source = "entity.nombre"),
    })
    RoleDto toDto(RoleEntity entity);
}
