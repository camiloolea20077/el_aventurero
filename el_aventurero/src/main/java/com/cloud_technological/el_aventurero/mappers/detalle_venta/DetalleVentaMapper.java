package com.cloud_technological.el_aventurero.mappers.detalle_venta;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.cloud_technological.el_aventurero.dto.detalle_venta.DetalleVentaDto;
import com.cloud_technological.el_aventurero.entity.DetalleVentaEntity;

@Mapper(componentModel = "spring")
public interface DetalleVentaMapper {

    @Mappings({
        @Mapping(target = "producto_nombre", ignore = true)
    })
    DetalleVentaDto toDto(DetalleVentaEntity entity);
}