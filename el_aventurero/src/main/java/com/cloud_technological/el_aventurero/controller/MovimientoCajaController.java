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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_technological.el_aventurero.dto.movimiento_caja.CierreSemanalDto;
import com.cloud_technological.el_aventurero.dto.movimiento_caja.CreateMovimientoDto;
import com.cloud_technological.el_aventurero.dto.movimiento_caja.MovimientoCajaDto;
import com.cloud_technological.el_aventurero.dto.movimiento_caja.ResumenFlujoDto;
import com.cloud_technological.el_aventurero.services.MovimientoCajaService;
import com.cloud_technological.el_aventurero.util.ApiResponse;

@RestController
@RequestMapping("/api/flujo-caja")
public class MovimientoCajaController {

    private final MovimientoCajaService movimientoCajaService;

    public MovimientoCajaController(MovimientoCajaService movimientoCajaService) {
        this.movimientoCajaService = movimientoCajaService;
    }

    @PostMapping("/movimiento")
    public ResponseEntity<ApiResponse<MovimientoCajaDto>> create(@Valid @RequestBody CreateMovimientoDto createDto) {
        try {
            MovimientoCajaDto result = movimientoCajaService.create(createDto);
            ApiResponse<MovimientoCajaDto> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Movimiento registrado correctamente",
                false,
                result
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @DeleteMapping("/movimiento/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") Long id) {
        try {
            Boolean isDeleted = movimientoCajaService.delete(id);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Movimiento eliminado correctamente",
                false,
                isDeleted
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @GetMapping("/movimiento/{id}")
    public ResponseEntity<ApiResponse<MovimientoCajaDto>> findById(@PathVariable Long id) {
        try {
            MovimientoCajaDto result = movimientoCajaService.findById(id);
            ApiResponse<MovimientoCajaDto> response = new ApiResponse<>(
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

    @GetMapping("/movimientos")
    public ResponseEntity<ApiResponse<List<MovimientoCajaDto>>> findByFechaBetween(
            @RequestParam("fecha_inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fecha_fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            List<MovimientoCajaDto> result = movimientoCajaService.findByFechaBetween(fechaInicio, fechaFin);
            ApiResponse<List<MovimientoCajaDto>> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Movimientos encontrados",
                false,
                result
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @GetMapping("/resumen")
    public ResponseEntity<ApiResponse<ResumenFlujoDto>> getResumenFlujo(
            @RequestParam("fecha_inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fecha_fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            ResumenFlujoDto result = movimientoCajaService.getResumenFlujo(fechaInicio, fechaFin);
            ApiResponse<ResumenFlujoDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Resumen generado correctamente",
                false,
                result
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }
    @GetMapping("/cierre-semanal")
    public ResponseEntity<ApiResponse<CierreSemanalDto>> getCierreSemanal(
            @RequestParam("fecha_inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fecha_fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        try {
            CierreSemanalDto result = movimientoCajaService.getCierreSemanal(fechaInicio, fechaFin);
            ApiResponse<CierreSemanalDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Cierre semanal generado correctamente",
                false,
                result
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }
}