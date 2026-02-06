package com.cloud_technological.el_aventurero.mappers.conteo_inventario;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.cloud_technological.el_aventurero.dto.conteo_inventario.AjusteInventarioDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.CreateAjusteDto;
import com.cloud_technological.el_aventurero.entity.AjusteInventarioEntity;

@Mapper(componentModel = "spring")
public interface AjusteInventarioMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "created_at", ignore = true),
            @Mapping(target = "usuario_id", ignore = true),
            @Mapping(target = "producto_id", source = "dto.producto_id"),
            @Mapping(target = "tipo", source = "dto.tipo"),
            @Mapping(target = "cantidad", source = "dto.cantidad"),
            @Mapping(target = "motivo", source = "dto.motivo"),
            @Mapping(target = "descripcion", source = "dto.descripcion"),
            @Mapping(target = "conteo_id", source = "dto.conteo_id"),
            @Mapping(target = "fecha", source = "dto.fecha")
    })
    AjusteInventarioEntity createToEntity(CreateAjusteDto dto);

    @Mappings({
            @Mapping(target = "id", source = "entity.id"),
            @Mapping(target = "producto_id", source = "entity.producto_id"),
            @Mapping(target = "producto_nombre", ignore = true),
            @Mapping(target = "tipo", source = "entity.tipo"),
            @Mapping(target = "cantidad", source = "entity.cantidad"),
            @Mapping(target = "motivo", source = "entity.motivo"),
            @Mapping(target = "descripcion", source = "entity.descripcion"),
            @Mapping(target = "conteo_id", source = "entity.conteo_id"),
            @Mapping(target = "usuario_id", source = "entity.usuario_id"),
            @Mapping(target = "fecha", source = "entity.fecha"),
            @Mapping(target = "created_at", source = "entity.created_at")
    })
    AjusteInventarioDto toDto(AjusteInventarioEntity entity);
}