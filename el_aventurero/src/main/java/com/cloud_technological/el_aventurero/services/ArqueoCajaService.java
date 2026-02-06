package com.cloud_technological.el_aventurero.services;

import java.time.LocalDate;
import java.util.List;

import com.cloud_technological.el_aventurero.dto.arqueo_caja.ArqueoCajaDto;
import com.cloud_technological.el_aventurero.dto.arqueo_caja.CreateArqueoDto;
import com.cloud_technological.el_aventurero.dto.arqueo_caja.DatosParaArqueoDto;
import com.cloud_technological.el_aventurero.dto.arqueo_caja.UpdateEstadoArqueoDto;

public interface ArqueoCajaService {
    ArqueoCajaDto create(CreateArqueoDto createDto);
    Boolean delete(Long id);
    ArqueoCajaDto findById(Long id);
    List<ArqueoCajaDto> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    ArqueoCajaDto findByFecha(LocalDate fecha);
    DatosParaArqueoDto getDatosParaArqueo(LocalDate fecha);
    ArqueoCajaDto updateEstado(Long id, UpdateEstadoArqueoDto updateDto);
}