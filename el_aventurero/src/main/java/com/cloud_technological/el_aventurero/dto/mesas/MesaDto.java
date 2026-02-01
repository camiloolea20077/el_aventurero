package com.cloud_technological.el_aventurero.dto.mesas;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MesaDto {
    private Long id;
    private Integer numero;
    private String estado;
    private BigDecimal total_acumulado;
    private Long activo;
}