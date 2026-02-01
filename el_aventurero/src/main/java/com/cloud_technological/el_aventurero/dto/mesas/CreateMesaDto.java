package com.cloud_technological.el_aventurero.dto.mesas;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateMesaDto {
    private Integer numero;
    private String estado; // LIBRE u OCUPADA
    private Long activo;
}