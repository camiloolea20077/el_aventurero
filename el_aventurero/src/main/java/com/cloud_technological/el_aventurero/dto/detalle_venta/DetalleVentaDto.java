package com.cloud_technological.el_aventurero.dto.detalle_venta;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleVentaDto {
    private Long id;
    private Long venta_id;
    private Long producto_id;
    private String producto_nombre;
    private Integer cantidad;
    private BigDecimal precio_unitario;
    private BigDecimal subtotal;
    private Long activo;
}