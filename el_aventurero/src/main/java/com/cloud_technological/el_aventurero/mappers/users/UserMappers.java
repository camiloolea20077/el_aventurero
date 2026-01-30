package com.cloud_technological.el_aventurero.mappers.users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.cloud_technological.el_aventurero.dto.users.CreateUserDto;
import com.cloud_technological.el_aventurero.dto.users.UserDto;
import com.cloud_technological.el_aventurero.entity.UserEntity;


@Mapper(componentModel = "spring")
public interface UserMappers {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "created_at", ignore = true),
            @Mapping(target = "updated_at", ignore = true),
            @Mapping(target = "rol_id", source="dto.rol_id"),
            @Mapping(target = "email", source = "dto.email"),
            @Mapping(target = "nombre", source = "dto.name"),
            @Mapping(target = "password", source = "dto.password"),
            @Mapping(target = "activo", source = "dto.active"),
            @Mapping(target = "permisos", source = "dto.permisos")
    })
    UserEntity createToEntity(CreateUserDto dto);
    @Mappings({
            @Mapping(target = "id", source = "entity.id"),
            @Mapping(target = "email", source = "entity.email"),
            @Mapping(target = "password", source = "entity.password"),
            @Mapping(target = "name", source = "entity.nombre"),
            @Mapping(target = "rol_id", source = "entity.rol_id"),
            @Mapping(target = "active", source = "entity.activo"),
            @Mapping(target = "permisos", source = "entity.permisos")
    })
    UserDto toDto(UserEntity entity);
}
