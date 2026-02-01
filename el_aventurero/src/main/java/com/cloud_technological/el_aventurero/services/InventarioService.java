package com.cloud_technological.el_aventurero.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.cloud_technological.el_aventurero.dto.inventario.AjusteStockDto;
import com.cloud_technological.el_aventurero.dto.inventario.CreateInventarioDto;
import com.cloud_technological.el_aventurero.dto.inventario.InventarioDto;
import com.cloud_technological.el_aventurero.dto.inventario.InventarioTableDto;
import com.cloud_technological.el_aventurero.dto.inventario.UpdateInventarioDto;
import com.cloud_technological.el_aventurero.util.PageableDto;

public interface InventarioService {
    InventarioDto create(CreateInventarioDto createDto);
    Boolean update(UpdateInventarioDto updateDto);
    Boolean delete(Long id);
    InventarioDto findById(Long id);
    InventarioDto findByProductoId(Long productoId);
    List<InventarioDto> findAllActive();
    Page<InventarioTableDto> pageInventario(PageableDto<Object> pageableDto);
    
    // Métodos especiales para ajustar stock
    Boolean ajustarStock(AjusteStockDto ajusteDto);
    Boolean sumarStock(Long productoId, Integer cantidad);
    Boolean restarStock(Long productoId, Integer cantidad);
}