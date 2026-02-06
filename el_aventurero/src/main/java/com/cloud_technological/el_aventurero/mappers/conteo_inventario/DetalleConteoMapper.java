package com.cloud_technological.el_aventurero.mappers.conteo_inventario;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.cloud_technological.el_aventurero.dto.conteo_inventario.CreateDetalleConteoDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.DetalleConteoDto;
import com.cloud_technological.el_aventurero.entity.DetalleConteoEntity;

@Mapper(componentModel = "spring")
public interface DetalleConteoMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "created_at", ignore = true),
            @Mapping(target = "stock_sistema", ignore = true),
            @Mapping(target = "diferencia", ignore = true),
            @Mapping(target = "motivo", ignore = true),
            @Mapping(target = "ajustado", ignore = true),
            @Mapping(target = "conteo_id", source = "dto.conteo_id"),
            @Mapping(target = "producto_id", source = "dto.producto_id"),
            @Mapping(target = "stock_fisico", source = "dto.stock_fisico")
    })
    DetalleConteoEntity createToEntity(CreateDetalleConteoDto dto);

    @Mappings({
            @Mapping(target = "id", source = "entity.id"),
            @Mapping(target = "conteo_id", source = "entity.conteo_id"),
            @Mapping(target = "producto_id", source = "entity.producto_id"),
            @Mapping(target = "producto_nombre", ignore = true),
            @Mapping(target = "stock_sistema", source = "entity.stock_sistema"),
            @Mapping(target = "stock_fisico", source = "entity.stock_fisico"),
            @Mapping(target = "diferencia", source = "entity.diferencia"),
            @Mapping(target = "motivo", source = "entity.motivo"),
            @Mapping(target = "ajustado", source = "entity.ajustado"),
            @Mapping(target = "created_at", source = "entity.created_at")
    })
    DetalleConteoDto toDto(DetalleConteoEntity entity);
}