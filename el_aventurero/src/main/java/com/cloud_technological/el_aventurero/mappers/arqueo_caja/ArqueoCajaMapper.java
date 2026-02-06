package com.cloud_technological.el_aventurero.mappers.arqueo_caja;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.cloud_technological.el_aventurero.dto.arqueo_caja.ArqueoCajaDto;
import com.cloud_technological.el_aventurero.dto.arqueo_caja.CreateArqueoDto;
import com.cloud_technological.el_aventurero.entity.ArqueoCajaEntity;

@Mapper(componentModel = "spring")
public interface ArqueoCajaMapper {
    
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "activo", ignore = true),
        @Mapping(target = "total_ingresos_sistema", ignore = true),
        @Mapping(target = "total_egresos_sistema", ignore = true),
        @Mapping(target = "saldo_esperado", ignore = true),
        @Mapping(target = "diferencia", ignore = true),
        @Mapping(target = "estado", ignore = true),
        @Mapping(target = "fecha", source = "dto.fecha"),
        @Mapping(target = "saldo_inicial", source = "dto.saldo_inicial"),
        @Mapping(target = "efectivo_real", source = "dto.efectivo_real"),
        @Mapping(target = "observaciones", source = "dto.observaciones"),
        @Mapping(target = "billetes_100000", source = "dto.billetes_100000"),
        @Mapping(target = "billetes_50000", source = "dto.billetes_50000"),
        @Mapping(target = "billetes_20000", source = "dto.billetes_20000"),
        @Mapping(target = "billetes_10000", source = "dto.billetes_10000"),
        @Mapping(target = "billetes_5000", source = "dto.billetes_5000"),
        @Mapping(target = "billetes_2000", source = "dto.billetes_2000"),
        @Mapping(target = "billetes_1000", source = "dto.billetes_1000"),
        @Mapping(target = "monedas_1000", source = "dto.monedas_1000"),
        @Mapping(target = "monedas_500", source = "dto.monedas_500"),
        @Mapping(target = "monedas_200", source = "dto.monedas_200"),
        @Mapping(target = "monedas_100", source = "dto.monedas_100"),
        @Mapping(target = "monedas_50", source = "dto.monedas_50")
    })
    ArqueoCajaEntity createToEntity(CreateArqueoDto dto);

    @Mappings({
        @Mapping(target = "id", source = "entity.id"),
        @Mapping(target = "fecha", source = "entity.fecha"),
        @Mapping(target = "saldo_inicial", source = "entity.saldo_inicial"),
        @Mapping(target = "total_ingresos_sistema", source = "entity.total_ingresos_sistema"),
        @Mapping(target = "total_egresos_sistema", source = "entity.total_egresos_sistema"),
        @Mapping(target = "saldo_esperado", source = "entity.saldo_esperado"),
        @Mapping(target = "efectivo_real", source = "entity.efectivo_real"),
        @Mapping(target = "diferencia", source = "entity.diferencia"),
        @Mapping(target = "estado", source = "entity.estado"),
        @Mapping(target = "observaciones", source = "entity.observaciones"),
        @Mapping(target = "billetes_100000", source = "entity.billetes_100000"),
        @Mapping(target = "billetes_50000", source = "entity.billetes_50000"),
        @Mapping(target = "billetes_20000", source = "entity.billetes_20000"),
        @Mapping(target = "billetes_10000", source = "entity.billetes_10000"),
        @Mapping(target = "billetes_5000", source = "entity.billetes_5000"),
        @Mapping(target = "billetes_2000", source = "entity.billetes_2000"),
        @Mapping(target = "billetes_1000", source = "entity.billetes_1000"),
        @Mapping(target = "monedas_1000", source = "entity.monedas_1000"),
        @Mapping(target = "monedas_500", source = "entity.monedas_500"),
        @Mapping(target = "monedas_200", source = "entity.monedas_200"),
        @Mapping(target = "monedas_100", source = "entity.monedas_100"),
        @Mapping(target = "monedas_50", source = "entity.monedas_50"),
        @Mapping(target = "activo", source = "entity.activo"),
        @Mapping(target = "created_at", source = "entity.created_at"),
        @Mapping(target = "updated_at", source = "entity.updated_at")
    })
    ArqueoCajaDto toDto(ArqueoCajaEntity entity);
}