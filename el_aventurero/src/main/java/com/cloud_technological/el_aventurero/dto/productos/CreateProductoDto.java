package com.cloud_technological.el_aventurero.dto.productos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductoDto {
    private String nombre;
    private String tipo_venta; // UNIDAD o BOTELLA
    private Long activo;
}