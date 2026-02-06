package com.cloud_technological.el_aventurero.services;

import java.time.LocalDate;
import java.util.List;

import com.cloud_technological.el_aventurero.dto.conteo_inventario.AjusteInventarioDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.ConteoInventarioDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.CreateAjusteDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.CreateConteoDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.CreateDetalleConteoDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.DetalleConteoDto;

public interface ConteoInventarioService {
    // Conteo
    ConteoInventarioDto iniciarConteo(CreateConteoDto createDto);

    ConteoInventarioDto completarConteo(Long conteoId);

    ConteoInventarioDto findById(Long id);

    List<ConteoInventarioDto> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);

    ConteoInventarioDto getLastConteo();

    // Detalles
    DetalleConteoDto registrarDetalle(CreateDetalleConteoDto createDto);

    List<DetalleConteoDto> getDetallesByConteoId(Long conteoId);

    List<DetalleConteoDto> getDiferencias(Long conteoId);

    List<DetalleConteoDto> getPendientesAjuste(Long conteoId);

    // Ajustes
    AjusteInventarioDto ajustarInventario(CreateAjusteDto createDto);

    List<AjusteInventarioDto> getAjustesByConteoId(Long conteoId);

    List<AjusteInventarioDto> getAjustesByProductoId(Long productoId);
}