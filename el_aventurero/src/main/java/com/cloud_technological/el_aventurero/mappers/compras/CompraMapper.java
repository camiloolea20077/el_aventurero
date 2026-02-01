package com.cloud_technological.el_aventurero.mappers.compras;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.cloud_technological.el_aventurero.dto.compras.CompraDto;
import com.cloud_technological.el_aventurero.entity.CompraEntity;

@Mapper(componentModel = "spring")
public interface CompraMapper {

    @Mappings({
        @Mapping(target = "detalles", ignore = true)
    })
    CompraDto toDto(CompraEntity entity);
}