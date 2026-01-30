package com.cloud_technological.el_aventurero.mappers.productos;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.MappingTarget;

import com.cloud_technological.el_aventurero.dto.productos.CreateProductoDto;
import com.cloud_technological.el_aventurero.dto.productos.ProductoDto;
import com.cloud_technological.el_aventurero.dto.productos.UpdateProductoDto;
import com.cloud_technological.el_aventurero.entity.ProductoEntity;

@Mapper(componentModel = "spring")
public interface ProductoMapper {
    
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "nombre", source = "dto.nombre"),
        @Mapping(target = "tipo_venta", source = "dto.tipo_venta"),
        @Mapping(target = "activo", source = "dto.activo")
    })
    ProductoEntity createToEntity(CreateProductoDto dto);

    ProductoDto toDto(ProductoEntity entity);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    void updateEntityFromDto(UpdateProductoDto dto, @MappingTarget ProductoEntity entity);
}