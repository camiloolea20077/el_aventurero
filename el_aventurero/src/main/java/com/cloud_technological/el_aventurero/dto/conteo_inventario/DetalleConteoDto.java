package com.cloud_technological.el_aventurero.dto.conteo_inventario;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetalleConteoDto {
    private Long id;
    private Long conteo_id;
    private Long producto_id;
    private String producto_nombre;
    private Integer stock_sistema;
    private Integer stock_fisico;
    private Integer diferencia;
    private String motivo;
    private Boolean ajustado;
    private LocalDateTime created_at;
}