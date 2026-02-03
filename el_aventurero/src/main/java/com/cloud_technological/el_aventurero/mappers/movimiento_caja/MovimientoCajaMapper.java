package com.cloud_technological.el_aventurero.mappers.movimiento_caja;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.cloud_technological.el_aventurero.dto.movimiento_caja.CreateMovimientoDto;
import com.cloud_technological.el_aventurero.dto.movimiento_caja.MovimientoCajaDto;
import com.cloud_technological.el_aventurero.entity.MovimientoCajaEntity;

@Mapper(componentModel = "spring")
public interface MovimientoCajaMapper {
    
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "tipo", source = "dto.tipo"),
        @Mapping(target = "concepto", source = "dto.concepto"),
        @Mapping(target = "categoria", source = "dto.categoria"),
        @Mapping(target = "monto", source = "dto.monto"),
        @Mapping(target = "metodo_pago", source = "dto.metodo_pago"),
        @Mapping(target = "descripcion", source = "dto.descripcion"),
        @Mapping(target = "fecha", source = "dto.fecha"),
        @Mapping(target = "venta_id", source = "dto.venta_id"),
        @Mapping(target = "compra_id", source = "dto.compra_id")
    })
    MovimientoCajaEntity createToEntity(CreateMovimientoDto dto);

    @Mappings({
        @Mapping(target = "id", source = "entity.id"),
        @Mapping(target = "tipo", source = "entity.tipo"),
        @Mapping(target = "concepto", source = "entity.concepto"),
        @Mapping(target = "categoria", source = "entity.categoria"),
        @Mapping(target = "monto", source = "entity.monto"),
        @Mapping(target = "metodo_pago", source = "entity.metodo_pago"),
        @Mapping(target = "descripcion", source = "entity.descripcion"),
        @Mapping(target = "fecha", source = "entity.fecha"),
        @Mapping(target = "venta_id", source = "entity.venta_id"),
        @Mapping(target = "compra_id", source = "entity.compra_id"),
        @Mapping(target = "activo", source = "entity.activo"),
        @Mapping(target = "created_at", source = "entity.created_at"),
        @Mapping(target = "updated_at", source = "entity.updated_at")
    })
    MovimientoCajaDto toDto(MovimientoCajaEntity entity);
}