package com.cloud_technological.el_aventurero.dto.productos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProductoDto {
    private Long id;
    private String nombre;
    private String tipo_venta;
    private Long activo;
}