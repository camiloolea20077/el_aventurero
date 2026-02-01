package com.cloud_technological.el_aventurero.dto.mesas;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MesaTableDto {
    private Long id;
    private Integer numero;
    private String estado;
    private BigDecimal total_acumulado;
    private Long activo;
}