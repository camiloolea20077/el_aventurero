package com.cloud_technological.el_aventurero.dto.ventas;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetodoPagoStatsDto {
    private String metodo_pago;
    private BigDecimal total;
    private Integer cantidad;
}
