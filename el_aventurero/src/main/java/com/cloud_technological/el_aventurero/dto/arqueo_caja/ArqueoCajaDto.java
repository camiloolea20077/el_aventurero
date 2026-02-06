package com.cloud_technological.el_aventurero.dto.arqueo_caja;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArqueoCajaDto {
    private Long id;
    private LocalDate fecha;
    private BigDecimal saldo_inicial;
    private BigDecimal total_ingresos_sistema;
    private BigDecimal total_egresos_sistema;
    private BigDecimal saldo_esperado;
    private BigDecimal efectivo_real;
    private BigDecimal diferencia;
    private String estado;
    private String observaciones;
    
    // Detalle del conteo
    private Integer billetes_100000;
    private Integer billetes_50000;
    private Integer billetes_20000;
    private Integer billetes_10000;
    private Integer billetes_5000;
    private Integer billetes_2000;
    private Integer billetes_1000;
    private Integer monedas_1000;
    private Integer monedas_500;
    private Integer monedas_200;
    private Integer monedas_100;
    private Integer monedas_50;
    
    private Long activo;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}