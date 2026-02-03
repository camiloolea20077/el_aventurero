package com.cloud_technological.el_aventurero.dto.movimiento_caja;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovimientoCajaDto {
    private Long id;
    private String tipo;
    private String concepto;
    private String categoria;
    private BigDecimal monto;
    private String metodo_pago;
    private String descripcion;
    private LocalDate fecha;
    private Long venta_id;
    private Long compra_id;
    private Long activo;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}