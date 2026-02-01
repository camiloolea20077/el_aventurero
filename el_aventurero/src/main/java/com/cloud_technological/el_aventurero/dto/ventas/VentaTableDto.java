package com.cloud_technological.el_aventurero.dto.ventas;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VentaTableDto {
    private Long id;
    private Long mesa_id;
    private Integer mesa_numero;
    private BigDecimal total;
    private String metodo_pago;
    private Integer cantidad_productos;
    private LocalDateTime created_at;
    private Long activo;
}