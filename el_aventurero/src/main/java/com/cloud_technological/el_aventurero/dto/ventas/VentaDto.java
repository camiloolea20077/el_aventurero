package com.cloud_technological.el_aventurero.dto.ventas;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.cloud_technological.el_aventurero.dto.detalle_venta.DetalleVentaDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VentaDto {
    private Long id;
    private Long mesa_id;
    private Integer mesa_numero;
    private BigDecimal total;
    private String metodo_pago;
    private Long activo;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private List<DetalleVentaDto> detalles;
}