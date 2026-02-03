package com.cloud_technological.el_aventurero.dto.movimiento_caja;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumenFlujoDto {
    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
    private BigDecimal total_ingresos;
    private BigDecimal total_egresos;
    private BigDecimal balance;
    private Long movimientos_ingreso;
    private Long movimientos_egreso;
}