package com.cloud_technological.el_aventurero.dto.ventas;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVentaDto {
    private Long mesa_id;
    private String metodo_pago;
}