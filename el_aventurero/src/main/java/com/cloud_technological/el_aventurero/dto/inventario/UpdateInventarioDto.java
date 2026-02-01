package com.cloud_technological.el_aventurero.dto.inventario;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateInventarioDto {
    private Long id;
    private Long producto_id;
    private Integer stock;
    private BigDecimal costo_unitario;
    private BigDecimal precio_venta;
    private Long activo;
}