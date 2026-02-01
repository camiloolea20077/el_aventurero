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

import com.cloud_technological.el_aventurero.dto.consumo_mesa.CreateConsumoMesaDto;
import com.cloud_technological.el_aventurero.dto.consumo_mesa.ConsumoMesaDto;
import com.cloud_technological.el_aventurero.dto.consumo_mesa.ConsumoMesaTableDto;
import com.cloud_technological.el_aventurero.dto.consumo_mesa.TotalMesaDto;
import com.cloud_technological.el_aventurero.dto.consumo_mesa.UpdateConsumoMesaDto;
import com.cloud_technological.el_aventurero.services.ConsumoMesaService;
import com.cloud_technological.el_aventurero.util.ApiResponse;
import com.cloud_technological.el_aventurero.util.GlobalException;
import com.cloud_technological.el_aventurero.util.PageableDto;



@RestController
@RequestMapping("/api/consumo-mesa")
public class ConsumoMesaController {

    private final ConsumoMesaService consumoMesaService;

    public ConsumoMesaController(ConsumoMesaService consumoMesaService) {
        this.consumoMesaService = consumoMesaService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ConsumoMesaDto>> create(@Valid @RequestBody CreateConsumoMesaDto createDto) {
        try {
            ConsumoMesaDto result = consumoMesaService.create(createDto);
            ApiResponse<ConsumoMesaDto> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Producto agregado a la mesa",
                false,
                result
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Object>> update(@Valid @RequestBody UpdateConsumoMesaDto updateDto) {
        try {
            Boolean isUpdated = consumoMesaService.update(updateDto);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Consumo actualizado correctamente",
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
            Boolean isDeleted = consumoMesaService.delete(id);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Consumo eliminado correctamente",
                false,
                isDeleted
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConsumoMesaDto>> findById(@PathVariable Long id) {
        try {
            ConsumoMesaDto result = consumoMesaService.findById(id);
            ApiResponse<ConsumoMesaDto> response = new ApiResponse<>(
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

    @GetMapping("/mesa/{mesaId}")
    public ResponseEntity<ApiResponse<Object>> findByMesaId(@PathVariable Long mesaId) {
        try {
            List<ConsumoMesaDto> result = consumoMesaService.findByMesaId(mesaId);
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

    @GetMapping("/mesa/{mesaId}/total")
    public ResponseEntity<ApiResponse<TotalMesaDto>> getTotalByMesaId(@PathVariable Long mesaId) {
        try {
            TotalMesaDto result = consumoMesaService.getTotalByMesaId(mesaId);
            ApiResponse<TotalMesaDto> response = new ApiResponse<>(
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

    @DeleteMapping("/mesa/{mesaId}")
    public ResponseEntity<ApiResponse<Object>> deleteByMesaId(@PathVariable Long mesaId) {
        try {
            Boolean isDeleted = consumoMesaService.deleteByMesaId(mesaId);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Consumos de la mesa eliminados",
                false,
                isDeleted
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @PostMapping("/page")
    public ResponseEntity<ApiResponse<Object>> page(@Valid @RequestBody PageableDto<Object> pageableDto) {
        try {
            Page<ConsumoMesaTableDto> result = consumoMesaService.pageConsumoMesa(pageableDto);
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