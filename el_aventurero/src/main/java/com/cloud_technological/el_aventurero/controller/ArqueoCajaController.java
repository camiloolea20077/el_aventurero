package com.cloud_technological.el_aventurero.controller;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_technological.el_aventurero.dto.arqueo_caja.ArqueoCajaDto;
import com.cloud_technological.el_aventurero.dto.arqueo_caja.CreateArqueoDto;
import com.cloud_technological.el_aventurero.dto.arqueo_caja.DatosParaArqueoDto;
import com.cloud_technological.el_aventurero.dto.arqueo_caja.UpdateEstadoArqueoDto;
import com.cloud_technological.el_aventurero.services.ArqueoCajaService;
import com.cloud_technological.el_aventurero.util.ApiResponse;

@RestController
@RequestMapping("/api/arqueo-caja")
public class ArqueoCajaController {

    private final ArqueoCajaService arqueoCajaService;

    public ArqueoCajaController(ArqueoCajaService arqueoCajaService) {
        this.arqueoCajaService = arqueoCajaService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ArqueoCajaDto>> create(@Valid @RequestBody CreateArqueoDto createDto) {
        try {
            ArqueoCajaDto result = arqueoCajaService.create(createDto);
            ApiResponse<ArqueoCajaDto> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Arqueo registrado correctamente",
                false,
                result
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") Long id) {
        try {
            Boolean isDeleted = arqueoCajaService.delete(id);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Arqueo eliminado correctamente",
                false,
                isDeleted
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ArqueoCajaDto>> findById(@PathVariable Long id) {
        try {
            ArqueoCajaDto result = arqueoCajaService.findById(id);
            ApiResponse<ArqueoCajaDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Elemento encontrado",
                false,
                result
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<ArqueoCajaDto>>> findByFechaBetween(
            @RequestParam("fecha_inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fecha_fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            List<ArqueoCajaDto> result = arqueoCajaService.findByFechaBetween(fechaInicio, fechaFin);
            ApiResponse<List<ArqueoCajaDto>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Arqueos encontrados",
                false,
                result
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @GetMapping("/del-dia")
    public ResponseEntity<ApiResponse<ArqueoCajaDto>> findByFecha(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            ArqueoCajaDto result = arqueoCajaService.findByFecha(fecha);
            ApiResponse<ArqueoCajaDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                result != null ? "Arqueo encontrado" : "No hay arqueo para esta fecha",
                false,
                result
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @GetMapping("/datos-dia")
    public ResponseEntity<ApiResponse<DatosParaArqueoDto>> getDatosParaArqueo(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        try {
            DatosParaArqueoDto result = arqueoCajaService.getDatosParaArqueo(fecha);
            ApiResponse<DatosParaArqueoDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Datos obtenidos correctamente",
                false,
                result
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }
    @PutMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<ArqueoCajaDto>> updateEstado(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEstadoArqueoDto updateDto) {
        try {
            ArqueoCajaDto result = arqueoCajaService.updateEstado(id, updateDto);
            ApiResponse<ArqueoCajaDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Estado actualizado correctamente",
                false,
                result
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }
}