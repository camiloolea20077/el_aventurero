package com.cloud_technological.el_aventurero.dto.consumo_mesa;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConsumoMesaDto {
    private Long id;
    private Long mesa_id;
    private Integer mesa_numero;
    private Long producto_id;
    private String producto_nombre;
    private String tipo_venta;
    private Integer cantidad;
    private Integer precio_unitario;
    private Integer subtotal;
    private Long activo;
}