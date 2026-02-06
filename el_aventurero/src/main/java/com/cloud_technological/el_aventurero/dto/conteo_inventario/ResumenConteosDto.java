package com.cloud_technological.el_aventurero.dto.conteo_inventario;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResumenConteosDto {
    private Integer total_conteos;
    private Integer conteos_completados;
    private Integer conteos_en_proceso;
    private Integer total_productos_contados;
    private Integer total_diferencias;
    private Integer total_ajustes;
    private Boolean conteo_realizado_semana; // Si hay al menos 1 conteo en la semana
}