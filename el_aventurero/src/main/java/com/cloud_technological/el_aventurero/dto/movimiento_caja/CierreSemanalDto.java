package com.cloud_technological.el_aventurero.dto.movimiento_caja;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CierreSemanalDto {
    private Integer semana;
    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
    private BigDecimal ventas_totales;
    private BigDecimal total_ingresos;
    private BigDecimal total_egresos;
    private BigDecimal balance_neto;
    private Long cantidad_ventas;
    private BigDecimal ticket_promedio;
    private List<MetodoPagoResumenDto> metodos_pago;
    private List<ProductoTopResumenDto> productos_top;
}