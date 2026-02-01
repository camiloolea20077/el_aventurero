package com.cloud_technological.el_aventurero.mappers.consumo_mesa;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.cloud_technological.el_aventurero.dto.consumo_mesa.ConsumoMesaDto;
import com.cloud_technological.el_aventurero.entity.ConsumoMesaEntity;

@Mapper(componentModel = "spring")
public interface ConsumoMesaMapper {

    @Mappings({
        @Mapping(target = "mesa_numero", ignore = true),
        @Mapping(target = "producto_nombre", ignore = true),
        @Mapping(target = "tipo_venta", ignore = true)
    })
    ConsumoMesaDto toDto(ConsumoMesaEntity entity);
}