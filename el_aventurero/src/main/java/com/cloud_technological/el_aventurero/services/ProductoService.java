package com.cloud_technological.el_aventurero.services;

import java.util.List;

import org.springframework.data.domain.PageImpl;

import com.cloud_technological.el_aventurero.dto.productos.CreateProductoDto;
import com.cloud_technological.el_aventurero.dto.productos.ProductoDto;
import com.cloud_technological.el_aventurero.dto.productos.ProductoTableDto;
import com.cloud_technological.el_aventurero.dto.productos.UpdateProductoDto;
import com.cloud_technological.el_aventurero.util.PageableDto;

public interface ProductoService {
    ProductoDto create(CreateProductoDto createDto);
    Boolean update(UpdateProductoDto updateDto);
    Boolean delete(Long id);
    ProductoDto findById(Long id);
    List<ProductoDto> findAllActive();
    PageImpl<ProductoTableDto> pageProductos(PageableDto<Object> pageableDto);
}