package com.cloud_technological.el_aventurero.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.cloud_technological.el_aventurero.dto.mesas.CreateMesaDto;
import com.cloud_technological.el_aventurero.dto.mesas.MesaDto;
import com.cloud_technological.el_aventurero.dto.mesas.MesaTableDto;
import com.cloud_technological.el_aventurero.dto.mesas.UpdateMesaDto;
import com.cloud_technological.el_aventurero.util.PageableDto;

public interface MesaService {
    MesaDto create(CreateMesaDto createDto);
    Boolean update(UpdateMesaDto updateDto);
    Boolean delete(Long id);
    MesaDto findById(Long id);
    List<MesaDto> findAllActive();
    List<MesaDto> findByEstado(String estado);
    Page<MesaTableDto> pageMesas(PageableDto<Object> pageableDto);
}