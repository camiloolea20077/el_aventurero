package com.cloud_technological.el_aventurero.dto.inventario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AjusteStockDto {
    private Long producto_id;
    private Integer cantidad;
    private String tipo; // "SUMA" o "RESTA"
}