package com.cloud_technological.el_aventurero.dto.detalle_compras;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleCompraDto {
    private Long id;
    private Long compra_id;
    private Long producto_id;
    private String producto_nombre;
    private Integer cajas;
    private Integer unidades_por_caja;
    private Integer total_unidades;
    private BigDecimal costo_total;
    private BigDecimal costo_unitario;
    private BigDecimal precio_sugerido;
    private BigDecimal precio_venta;
    private Long activo;
}