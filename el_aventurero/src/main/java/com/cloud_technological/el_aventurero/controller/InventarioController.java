package com.cloud_technological.el_aventurero.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_technological.el_aventurero.dto.inventario.AjusteStockDto;
import com.cloud_technological.el_aventurero.dto.inventario.CreateInventarioDto;
import com.cloud_technological.el_aventurero.dto.inventario.InventarioDto;
import com.cloud_technological.el_aventurero.dto.inventario.InventarioTableDto;
import com.cloud_technological.el_aventurero.dto.inventario.UpdateInventarioDto;
import com.cloud_technological.el_aventurero.services.InventarioService;
import com.cloud_technological.el_aventurero.util.ApiResponse;
import com.cloud_technological.el_aventurero.util.GlobalException;
import com.cloud_technological.el_aventurero.util.PageableDto;



@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<InventarioDto>> create(@Valid @RequestBody CreateInventarioDto createDto) {
        try {
            InventarioDto result = inventarioService.create(createDto);
            ApiResponse<InventarioDto> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Inventario creado correctamente",
                false,
                result
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Object>> update(@Valid @RequestBody UpdateInventarioDto updateDto) {
        try {
            Boolean isUpdated = inventarioService.update(updateDto);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Inventario actualizado correctamente",
                false,
                isUpdated
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") Long id) {
        try {
            Boolean isDeleted = inventarioService.delete(id);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Inventario eliminado correctamente",
                false,
                isDeleted
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InventarioDto>> findById(@PathVariable Long id) {
        try {
            InventarioDto result = inventarioService.findById(id);
            ApiResponse<InventarioDto> response = new ApiResponse<>(
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

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<ApiResponse<InventarioDto>> findByProductoId(@PathVariable Long productoId) {
        try {
            InventarioDto result = inventarioService.findByProductoId(productoId);
            ApiResponse<InventarioDto> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Inventario encontrado",
                false,
                result
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Object>> findAll() {
        try {
            List<InventarioDto> result = inventarioService.findAllActive();
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

    @PostMapping("/page")
    public ResponseEntity<ApiResponse<Object>> page(@Valid @RequestBody PageableDto<Object> pageableDto) {
        try {
            Page<InventarioTableDto> result = inventarioService.pageInventario(pageableDto);
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

    @PostMapping("/ajustar-stock")
    public ResponseEntity<ApiResponse<Object>> ajustarStock(@Valid @RequestBody AjusteStockDto ajusteDto) {
        try {
            Boolean result = inventarioService.ajustarStock(ajusteDto);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Stock ajustado correctamente",
                false,
                result
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }
}