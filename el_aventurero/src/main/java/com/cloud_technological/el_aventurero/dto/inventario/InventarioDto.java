package com.cloud_technological.el_aventurero.dto.inventario;


import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventarioDto {
    private Long id;
    private Long producto_id;
    private String producto_nombre;
    private String tipo_venta;
    private Integer stock;
    private BigDecimal costo_unitario;
    private BigDecimal precio_venta;
    private Long activo;
}