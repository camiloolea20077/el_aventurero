package com.cloud_technological.el_aventurero.mappers.ventas;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.cloud_technological.el_aventurero.dto.ventas.VentaDto;
import com.cloud_technological.el_aventurero.entity.VentaEntity;

@Mapper(componentModel = "spring")
public interface VentaMapper {

    @Mappings({
        @Mapping(target = "mesa_numero", ignore = true),
        @Mapping(target = "detalles", ignore = true)
    })
    VentaDto toDto(VentaEntity entity);
}