package com.cloud_technological.el_aventurero.dto.productos;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductoDto {
    private String nombre;
    private String tipo_venta; // UNIDAD o BOTELLA
    @NotBlank(message = "El tipo es requerido")
    private String tipo = "PRODUCTO"; // PRODUCTO | INSUMO
    private String categoria;
    private String unidad_medida;
    private Long activo;
}