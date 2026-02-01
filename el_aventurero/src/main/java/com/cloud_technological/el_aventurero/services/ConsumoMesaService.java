package com.cloud_technological.el_aventurero.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.cloud_technological.el_aventurero.dto.consumo_mesa.CreateConsumoMesaDto;
import com.cloud_technological.el_aventurero.dto.consumo_mesa.ConsumoMesaDto;
import com.cloud_technological.el_aventurero.dto.consumo_mesa.ConsumoMesaTableDto;
import com.cloud_technological.el_aventurero.dto.consumo_mesa.TotalMesaDto;
import com.cloud_technological.el_aventurero.dto.consumo_mesa.UpdateConsumoMesaDto;
import com.cloud_technological.el_aventurero.util.PageableDto;

public interface ConsumoMesaService {
    ConsumoMesaDto create(CreateConsumoMesaDto createDto);
    Boolean update(UpdateConsumoMesaDto updateDto);
    Boolean delete(Long id);
    ConsumoMesaDto findById(Long id);
    List<ConsumoMesaDto> findByMesaId(Long mesaId);
    TotalMesaDto getTotalByMesaId(Long mesaId);
    Page<ConsumoMesaTableDto> pageConsumoMesa(PageableDto<Object> pageableDto);
    Boolean deleteByMesaId(Long mesaId);
}