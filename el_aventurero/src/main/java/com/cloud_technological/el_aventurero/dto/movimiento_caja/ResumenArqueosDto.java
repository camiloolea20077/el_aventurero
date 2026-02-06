package com.cloud_technological.el_aventurero.dto.movimiento_caja;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumenArqueosDto {
    private Integer total_dias;
    private Integer arqueos_realizados;
    private Integer arqueos_cuadrados;
    private Integer arqueos_pendientes;
    private Integer arqueos_ajustados;
    private BigDecimal total_diferencias;
    private BigDecimal total_sobrantes;
    private BigDecimal total_faltantes;
}
