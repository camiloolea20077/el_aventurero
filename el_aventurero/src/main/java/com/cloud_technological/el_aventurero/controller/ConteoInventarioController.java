package com.cloud_technological.el_aventurero.controller;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_technological.el_aventurero.dto.conteo_inventario.AjusteInventarioDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.ConteoInventarioDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.CreateAjusteDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.CreateConteoDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.CreateDetalleConteoDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.DetalleConteoDto;
import com.cloud_technological.el_aventurero.services.ConteoInventarioService;
import com.cloud_technological.el_aventurero.util.ApiResponse;

@RestController
@RequestMapping("/api/conteo-inventario")
public class ConteoInventarioController {

    private final ConteoInventarioService conteoInventarioService;

    public ConteoInventarioController(ConteoInventarioService conteoInventarioService) {
        this.conteoInventarioService = conteoInventarioService;
    }

    // ==================== CONTEO ====================

    /**
     * Iniciar un nuevo conteo de inventario
     * POST /api/conteo-inventario/iniciar
     */
    @PostMapping("/iniciar")
    public ResponseEntity<ApiResponse<ConteoInventarioDto>> iniciarConteo(
            @Valid @RequestBody CreateConteoDto createDto) {
        try {
            ConteoInventarioDto result = conteoInventarioService.iniciarConteo(createDto);
            ApiResponse<ConteoInventarioDto> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Conteo de inventario iniciado correctamente",
                    false,
                    result);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Completar un conteo de inventario
     * PUT /api/conteo-inventario/{id}/completar
     */
    @PutMapping("/{id}/completar")
    public ResponseEntity<ApiResponse<ConteoInventarioDto>> completarConteo(@PathVariable Long id) {
        try {
            ConteoInventarioDto result = conteoInventarioService.completarConteo(id);
            ApiResponse<ConteoInventarioDto> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Conteo completado correctamente",
                    false,
                    result);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Obtener un conteo por ID
     * GET /api/conteo-inventario/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConteoInventarioDto>> findById(@PathVariable Long id) {
        try {
            ConteoInventarioDto result = conteoInventarioService.findById(id);
            ApiResponse<ConteoInventarioDto> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Conteo encontrado",
                    false,
                    result);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Listar conteos por rango de fechas
     * GET /api/conteo-inventario/list?fecha_inicio=2026-01-01&fecha_fin=2026-12-31
     */
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<ConteoInventarioDto>>> findByFechaBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha_inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha_fin) {
        try {
            List<ConteoInventarioDto> result = conteoInventarioService.findByFechaBetween(fecha_inicio, fecha_fin);
            ApiResponse<List<ConteoInventarioDto>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Conteos encontrados",
                    false,
                    result);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Obtener el último conteo realizado
     * GET /api/conteo-inventario/ultimo
     */
    @GetMapping("/ultimo")
    public ResponseEntity<ApiResponse<ConteoInventarioDto>> getLastConteo() {
        try {
            ConteoInventarioDto result = conteoInventarioService.getLastConteo();
            ApiResponse<ConteoInventarioDto> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    result != null ? "Último conteo encontrado" : "No hay conteos registrados",
                    false,
                    result);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    // ==================== DETALLES ====================

    /**
     * Registrar detalle de conteo (contar un producto)
     * POST /api/conteo-inventario/detalle
     */
    @PostMapping("/detalle")
    public ResponseEntity<ApiResponse<DetalleConteoDto>> registrarDetalle(
            @Valid @RequestBody CreateDetalleConteoDto createDto) {
        try {
            DetalleConteoDto result = conteoInventarioService.registrarDetalle(createDto);
            ApiResponse<DetalleConteoDto> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Detalle registrado correctamente",
                    false,
                    result);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Obtener todos los detalles de un conteo
     * GET /api/conteo-inventario/{id}/detalles
     */
    @GetMapping("/{id}/detalles")
    public ResponseEntity<ApiResponse<List<DetalleConteoDto>>> getDetalles(@PathVariable Long id) {
        try {
            List<DetalleConteoDto> result = conteoInventarioService.getDetallesByConteoId(id);
            ApiResponse<List<DetalleConteoDto>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Detalles encontrados",
                    false,
                    result);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Obtener solo productos con diferencias
     * GET /api/conteo-inventario/{id}/diferencias
     */
    @GetMapping("/{id}/diferencias")
    public ResponseEntity<ApiResponse<List<DetalleConteoDto>>> getDiferencias(@PathVariable Long id) {
        try {
            List<DetalleConteoDto> result = conteoInventarioService.getDiferencias(id);
            ApiResponse<List<DetalleConteoDto>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Diferencias encontradas",
                    false,
                    result);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Obtener productos pendientes de ajustar
     * GET /api/conteo-inventario/{id}/pendientes
     */
    @GetMapping("/{id}/pendientes")
    public ResponseEntity<ApiResponse<List<DetalleConteoDto>>> getPendientesAjuste(@PathVariable Long id) {
        try {
            List<DetalleConteoDto> result = conteoInventarioService.getPendientesAjuste(id);
            ApiResponse<List<DetalleConteoDto>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Pendientes de ajuste encontrados",
                    false,
                    result);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    // ==================== AJUSTES ====================

    /**
     * Ajustar inventario (resolver diferencia)
     * POST /api/conteo-inventario/ajustar
     */
    @PostMapping("/ajustar")
    public ResponseEntity<ApiResponse<AjusteInventarioDto>> ajustarInventario(
            @Valid @RequestBody CreateAjusteDto createDto) {
        try {
            AjusteInventarioDto result = conteoInventarioService.ajustarInventario(createDto);
            ApiResponse<AjusteInventarioDto> response = new ApiResponse<>(
                    HttpStatus.CREATED.value(),
                    "Inventario ajustado correctamente",
                    false,
                    result);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Obtener ajustes de un conteo
     * GET /api/conteo-inventario/{id}/ajustes
     */
    @GetMapping("/{id}/ajustes")
    public ResponseEntity<ApiResponse<List<AjusteInventarioDto>>> getAjustesByConteoId(@PathVariable Long id) {
        try {
            List<AjusteInventarioDto> result = conteoInventarioService.getAjustesByConteoId(id);
            ApiResponse<List<AjusteInventarioDto>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Ajustes encontrados",
                    false,
                    result);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Obtener historial de ajustes de un producto
     * GET /api/conteo-inventario/producto/{productoId}/ajustes
     */
    @GetMapping("/producto/{productoId}/ajustes")
    public ResponseEntity<ApiResponse<List<AjusteInventarioDto>>> getAjustesByProductoId(
            @PathVariable Long productoId) {
        try {
            List<AjusteInventarioDto> result = conteoInventarioService.getAjustesByProductoId(productoId);
            ApiResponse<List<AjusteInventarioDto>> response = new ApiResponse<>(
                    HttpStatus.OK.value(),
                    "Historial de ajustes encontrado",
                    false,
                    result);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }
}