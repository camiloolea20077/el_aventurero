package com.cloud_technological.el_aventurero.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_technological.el_aventurero.dto.ventas.CreateVentaDto;
import com.cloud_technological.el_aventurero.dto.ventas.VentaDto;
import com.cloud_technological.el_aventurero.dto.ventas.VentaTableDto;
import com.cloud_technological.el_aventurero.services.VentaService;
import com.cloud_technological.el_aventurero.util.ApiResponse;
import com.cloud_technological.el_aventurero.util.GlobalException;
import com.cloud_technological.el_aventurero.util.PageableDto;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<VentaDto>> create(@Valid @RequestBody CreateVentaDto createDto) {
        try {
            VentaDto result = ventaService.create(createDto);
            ApiResponse<VentaDto> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Venta registrada correctamente",
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
            Boolean isDeleted = ventaService.delete(id);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Venta eliminada correctamente",
                false,
                isDeleted
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VentaDto>> findById(@PathVariable Long id) {
        try {
            VentaDto result = ventaService.findById(id);
            ApiResponse<VentaDto> response = new ApiResponse<>(
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

    @PostMapping("/page")
    public ResponseEntity<ApiResponse<Object>> page(@Valid @RequestBody PageableDto<Object> pageableDto) {
        try {
            Page<VentaTableDto> result = ventaService.pageVentas(pageableDto);
            if (result.isEmpty()) {
                throw new GlobalException(HttpStatus.PARTIAL_CONTENT, "No se encontraron registros");
            }
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "",
                false,
                result
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }
}