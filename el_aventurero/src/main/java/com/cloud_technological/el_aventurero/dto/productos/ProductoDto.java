package com.cloud_technological.el_aventurero.dto.productos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoDto {
    private Long id;
    private String nombre;
    private String tipo_venta;
    private String tipo;
    private String categoria;
    private String unidad_medida;
    private Long activo;
}