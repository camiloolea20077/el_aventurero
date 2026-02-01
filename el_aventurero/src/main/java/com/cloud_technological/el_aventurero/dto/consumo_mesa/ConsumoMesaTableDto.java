package com.cloud_technological.el_aventurero.dto.consumo_mesa;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumoMesaTableDto {
    private Long id;
    private Long mesa_id;
    private Integer mesa_numero;
    private Long producto_id;
    private String producto_nombre;
    private String tipo_venta;
    private Integer cantidad;
    private String precio_unitario;
    private String subtotal;
    private Long activo;
    private Long total_rows;
}