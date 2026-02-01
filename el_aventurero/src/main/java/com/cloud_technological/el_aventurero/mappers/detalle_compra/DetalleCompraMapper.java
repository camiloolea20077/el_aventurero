package com.cloud_technological.el_aventurero.mappers.detalle_compra;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.cloud_technological.el_aventurero.dto.detalle_compras.DetalleCompraDto;
import com.cloud_technological.el_aventurero.entity.DetalleCompraEntity;

@Mapper(componentModel = "spring")
public interface DetalleCompraMapper {

    @Mappings({
        @Mapping(target = "producto_nombre", ignore = true)
    })
    DetalleCompraDto toDto(DetalleCompraEntity entity);
}