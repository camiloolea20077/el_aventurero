package com.cloud_technological.el_aventurero.dto.movimiento_caja;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetodoPagoResumenDto {
    private String metodo;
    private Long cantidad;
    private BigDecimal total;
    private BigDecimal porcentaje;
}