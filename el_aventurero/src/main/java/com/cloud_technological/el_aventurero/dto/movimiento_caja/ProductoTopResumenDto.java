package com.cloud_technological.el_aventurero.dto.movimiento_caja;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoTopResumenDto {
    private String producto_nombre;
    private Long cantidad_vendida;
    private BigDecimal total_vendido;
}