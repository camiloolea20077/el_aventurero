package com.cloud_technological.el_aventurero.dto.movimiento_caja;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMovimientoDto {

    private String tipo; // INGRESO, EGRESO
    private String concepto;
    private String categoria;
    private BigDecimal monto;
    private String metodo_pago;
    private String descripcion;
    private LocalDate fecha;
    private Long venta_id;
    private Long compra_id;
}