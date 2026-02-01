package com.cloud_technological.el_aventurero.dto.compras;

import java.math.BigDecimal;
import java.util.List;

import com.cloud_technological.el_aventurero.dto.detalle_compras.DetalleCompraDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompraDto {
    private Long id;
    private BigDecimal total_compra;
    private String metodo_pago;
    private Long activo;
    private List<DetalleCompraDto> detalles;
}