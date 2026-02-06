package com.cloud_technological.el_aventurero.mappers.conteo_inventario;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.cloud_technological.el_aventurero.dto.conteo_inventario.ConteoInventarioDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.CreateConteoDto;
import com.cloud_technological.el_aventurero.entity.ConteoInventarioEntity;

@Mapper(componentModel = "spring")
public interface ConteoInventarioMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "created_at", ignore = true),
            @Mapping(target = "updated_at", ignore = true),
            @Mapping(target = "deleted_at", ignore = true),
            @Mapping(target = "estado", ignore = true),
            @Mapping(target = "fecha", source = "dto.fecha"),
            @Mapping(target = "tipo", source = "dto.tipo")
    })
    ConteoInventarioEntity createToEntity(CreateConteoDto dto);

    @Mappings({
            @Mapping(target = "id", source = "entity.id"),
            @Mapping(target = "fecha", source = "entity.fecha"),
            @Mapping(target = "tipo", source = "entity.tipo"),
            @Mapping(target = "estado", source = "entity.estado"),
            @Mapping(target = "created_at", source = "entity.created_at"),
            @Mapping(target = "updated_at", source = "entity.updated_at")
    })
    ConteoInventarioDto toDto(ConteoInventarioEntity entity);
}