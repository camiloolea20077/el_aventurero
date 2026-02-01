package com.cloud_technological.el_aventurero.mappers.inventario;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.MappingTarget;

import com.cloud_technological.el_aventurero.dto.inventario.CreateInventarioDto;
import com.cloud_technological.el_aventurero.dto.inventario.InventarioDto;
import com.cloud_technological.el_aventurero.dto.inventario.UpdateInventarioDto;
import com.cloud_technological.el_aventurero.entity.InventarioEntity;

@Mapper(componentModel = "spring")
public interface InventarioMapper {
    
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "producto_id", source = "dto.producto_id"),
        @Mapping(target = "stock", source = "dto.stock"),
        @Mapping(target = "costo_unitario", source = "dto.costo_unitario"),
        @Mapping(target = "precio_venta", source = "dto.precio_venta"),
        @Mapping(target = "activo", source = "dto.activo")
    })
    InventarioEntity createToEntity(CreateInventarioDto dto);

    @Mappings({
        @Mapping(target = "producto_nombre", ignore = true),
        @Mapping(target = "tipo_venta", ignore = true)
    })
    InventarioDto toDto(InventarioEntity entity);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    void updateEntityFromDto(UpdateInventarioDto dto, @MappingTarget InventarioEntity entity);
}