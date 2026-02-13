package com.cloud_technological.el_aventurero.dto.productos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoTableDto {
    private Long id;
    private String nombre;
    private String tipo_venta;
    private String tipo;          // PRODUCTO | INSUMO
    private String categoria;     // Clasificación
    private String unidad_medida; // Opcional
    private Long activo;
}