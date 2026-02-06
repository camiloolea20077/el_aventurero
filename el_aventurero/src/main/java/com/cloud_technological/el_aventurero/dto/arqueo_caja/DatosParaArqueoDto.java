package com.cloud_technological.el_aventurero.dto.arqueo_caja;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatosParaArqueoDto {
    private BigDecimal saldo_inicial;
    private BigDecimal total_ingresos;
    private BigDecimal total_egresos;
    private BigDecimal saldo_esperado;
}
