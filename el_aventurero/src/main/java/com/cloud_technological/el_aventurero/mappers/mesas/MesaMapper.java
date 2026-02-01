package com.cloud_technological.el_aventurero.mappers.mesas;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.MappingTarget;

import com.cloud_technological.el_aventurero.dto.mesas.CreateMesaDto;
import com.cloud_technological.el_aventurero.dto.mesas.MesaDto;
import com.cloud_technological.el_aventurero.dto.mesas.UpdateMesaDto;
import com.cloud_technological.el_aventurero.entity.MesaEntity;

@Mapper(componentModel = "spring")
public interface MesaMapper {
    
    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true),
        @Mapping(target = "total_acumulado", ignore = true),
        @Mapping(target = "numero", source = "dto.numero"),
        @Mapping(target = "estado", source = "dto.estado"),
        @Mapping(target = "activo", source = "dto.activo")
    })
    MesaEntity createToEntity(CreateMesaDto dto);

    MesaDto toDto(MesaEntity entity);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "created_at", ignore = true),
        @Mapping(target = "updated_at", ignore = true),
        @Mapping(target = "deleted_at", ignore = true)
    })
    void updateEntityFromDto(UpdateMesaDto dto, @MappingTarget MesaEntity entity);
}