package com.cloud_technological.el_aventurero.services;

import org.springframework.data.domain.Page;

import com.cloud_technological.el_aventurero.dto.compras.CompraDto;
import com.cloud_technological.el_aventurero.dto.compras.CompraTableDto;
import com.cloud_technological.el_aventurero.dto.compras.CreateCompraDto;
import com.cloud_technological.el_aventurero.util.PageableDto;

public interface CompraService {
    CompraDto create(CreateCompraDto createDto);
    Boolean delete(Long id);
    CompraDto findById(Long id);
    Page<CompraTableDto> pageCompras(PageableDto<Object> pageableDto);
}