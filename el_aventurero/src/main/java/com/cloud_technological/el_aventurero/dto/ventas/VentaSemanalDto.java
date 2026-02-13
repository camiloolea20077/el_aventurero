package com.cloud_technological.el_aventurero.dto.ventas;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VentaSemanalDto {
    private String dia;
    private BigDecimal total;
    private Integer cantidad_ventas;
}
