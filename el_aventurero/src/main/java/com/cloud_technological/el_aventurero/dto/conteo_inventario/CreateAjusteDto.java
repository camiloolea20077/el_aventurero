package com.cloud_technological.el_aventurero.dto.conteo_inventario;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAjusteDto {
    private Long producto_id;
    private String tipo;
    private Integer cantidad;
    private String motivo;
    private String descripcion;
    private Long conteo_id;
    private LocalDate fecha;
}