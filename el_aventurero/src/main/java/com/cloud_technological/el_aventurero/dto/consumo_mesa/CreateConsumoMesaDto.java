package com.cloud_technological.el_aventurero.dto.consumo_mesa;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateConsumoMesaDto {
    private Long mesa_id;
    private Long producto_id;
    private Integer cantidad;
}