package com.cloud_technological.el_aventurero.dto.inventario;


import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventarioTableDto {
    private Long id;
    private Long producto_id;
    private String producto_nombre;
    private String tipo_venta;
    private Integer stock;
    private String costo_unitario;
    private String precio_venta;
    private String valor_total;
    private Long activo;

}