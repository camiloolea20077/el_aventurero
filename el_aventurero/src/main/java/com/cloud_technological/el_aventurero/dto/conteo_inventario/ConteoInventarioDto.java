package com.cloud_technological.el_aventurero.dto.conteo_inventario;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConteoInventarioDto {
    private Long id;
    private LocalDate fecha;
    private String tipo;
    private String estado;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}