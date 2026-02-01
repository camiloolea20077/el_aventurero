package com.cloud_technological.el_aventurero.dto.compras;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompraTableDto {
    private Long id;
    private String total_compra;
    private String metodo_pago;
    private Integer cantidad_productos;
    private LocalDateTime created_at;
    private Long activo;
}