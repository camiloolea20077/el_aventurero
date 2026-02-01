package com.cloud_technological.el_aventurero.services;

import org.springframework.data.domain.Page;

import com.cloud_technological.el_aventurero.dto.ventas.CreateVentaDto;
import com.cloud_technological.el_aventurero.dto.ventas.VentaDto;
import com.cloud_technological.el_aventurero.dto.ventas.VentaTableDto;
import com.cloud_technological.el_aventurero.util.PageableDto;

public interface VentaService {
    VentaDto create(CreateVentaDto createDto);
    Boolean delete(Long id);
    VentaDto findById(Long id);
    Page<VentaTableDto> pageVentas(PageableDto<Object> pageableDto);
}