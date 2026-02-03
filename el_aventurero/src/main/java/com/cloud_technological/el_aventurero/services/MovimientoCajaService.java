package com.cloud_technological.el_aventurero.services;

import java.time.LocalDate;
import java.util.List;

import com.cloud_technological.el_aventurero.dto.movimiento_caja.CierreSemanalDto;
import com.cloud_technological.el_aventurero.dto.movimiento_caja.CreateMovimientoDto;
import com.cloud_technological.el_aventurero.dto.movimiento_caja.MovimientoCajaDto;
import com.cloud_technological.el_aventurero.dto.movimiento_caja.ResumenFlujoDto;

public interface MovimientoCajaService {
    MovimientoCajaDto create(CreateMovimientoDto createDto);
    Boolean delete(Long id);
    MovimientoCajaDto findById(Long id);
    List<MovimientoCajaDto> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    ResumenFlujoDto getResumenFlujo(LocalDate fechaInicio, LocalDate fechaFin);
    CierreSemanalDto getCierreSemanal(LocalDate fechaInicio, LocalDate fechaFin);
}