package com.cloud_technological.el_aventurero.dto.conteo_inventario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateDetalleConteoDto {
    private Long conteo_id;
    private Long producto_id;
    private Integer stock_fisico;
}