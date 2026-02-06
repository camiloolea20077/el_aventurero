package com.cloud_technological.el_aventurero.dto.conteo_inventario;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateConteoDto {
    private LocalDate fecha;
    private String tipo;
}