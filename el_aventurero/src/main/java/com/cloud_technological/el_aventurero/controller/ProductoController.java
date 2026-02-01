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

import com.cloud_technological.el_aventurero.dto.productos.CreateProductoDto;
import com.cloud_technological.el_aventurero.dto.productos.ProductoDto;
import com.cloud_technological.el_aventurero.dto.productos.ProductoTableDto;
import com.cloud_technological.el_aventurero.dto.productos.UpdateProductoDto;
import com.cloud_technological.el_aventurero.services.ProductoService;
import com.cloud_technological.el_aventurero.util.ApiResponse;
import com.cloud_technological.el_aventurero.util.GlobalException;
import com.cloud_technological.el_aventurero.util.PageableDto;


@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ProductoDto>> create(@Valid @RequestBody CreateProductoDto createDto) {
        try {
            ProductoDto result = productoService.create(createDto);
            ApiResponse<ProductoDto> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Producto creado correctamente",
                false,
                result
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Object>> update(@Valid @RequestBody UpdateProductoDto updateDto) {
        try {
            Boolean isUpdated = productoService.update(updateDto);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Producto actualizado correctamente",
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
            Boolean isDeleted = productoService.delete(id);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Producto eliminado correctamente",
                false,
                isDeleted
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoDto>> findById(@PathVariable Long id) {
        try {
            ProductoDto result = productoService.findById(id);
            ApiResponse<ProductoDto> response = new ApiResponse<>(
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
    public ResponseEntity<ApiResponse<Object>> findAll() {
        try {
            List<ProductoDto> result = productoService.findAllActive();
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
            Page<ProductoTableDto> result = productoService.pageProductos(pageableDto);
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