package com.cloud_technological.el_aventurero.dto.detalle_compras;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDetalleCompraDto {
    private Long producto_id;
    private Integer cajas;
    private Integer unidades_por_caja;
    private BigDecimal costo_total;
    private BigDecimal precio_sugerido;
    private BigDecimal precio_venta;
}