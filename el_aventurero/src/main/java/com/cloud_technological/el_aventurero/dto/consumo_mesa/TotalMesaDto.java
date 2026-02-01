package com.cloud_technological.el_aventurero.dto.consumo_mesa;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TotalMesaDto {
    private Long mesa_id;
    private Integer mesa_numero;
    private String estado;
    private BigDecimal total;
}