package com.cloud_technological.el_aventurero.dto.compras;

import java.util.List;

import com.cloud_technological.el_aventurero.dto.detalle_compras.CreateDetalleCompraDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCompraDto {
    private String metodo_pago;
    private List<CreateDetalleCompraDto> detalles;
}