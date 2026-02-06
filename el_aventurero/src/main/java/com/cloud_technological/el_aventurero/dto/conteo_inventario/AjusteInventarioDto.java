package com.cloud_technological.el_aventurero.dto.conteo_inventario;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AjusteInventarioDto {
    private Long id;
    private Long producto_id;
    private String producto_nombre;
    private String tipo;
    private Integer cantidad;
    private String motivo;
    private String descripcion;
    private Long conteo_id;
    private Long usuario_id;
    private LocalDate fecha;
    private LocalDateTime created_at;
}