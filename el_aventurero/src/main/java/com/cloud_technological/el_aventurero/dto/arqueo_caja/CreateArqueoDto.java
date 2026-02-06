package com.cloud_technological.el_aventurero.dto.arqueo_caja;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateArqueoDto {
    private LocalDate fecha;
    private BigDecimal saldo_inicial;
    private BigDecimal efectivo_real;
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
}