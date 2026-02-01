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

import com.cloud_technological.el_aventurero.dto.mesas.CreateMesaDto;
import com.cloud_technological.el_aventurero.dto.mesas.MesaDto;
import com.cloud_technological.el_aventurero.dto.mesas.MesaTableDto;
import com.cloud_technological.el_aventurero.dto.mesas.UpdateMesaDto;
import com.cloud_technological.el_aventurero.services.MesaService;
import com.cloud_technological.el_aventurero.util.ApiResponse;
import com.cloud_technological.el_aventurero.util.GlobalException;
import com.cloud_technological.el_aventurero.util.PageableDto;



@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    private final MesaService mesaService;

    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<MesaDto>> create(@Valid @RequestBody CreateMesaDto createDto) {
        try {
            MesaDto result = mesaService.create(createDto);
            ApiResponse<MesaDto> response = new ApiResponse<>(
                HttpStatus.CREATED.value(),
                "Mesa creada correctamente",
                false,
                result
            );
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Object>> update(@Valid @RequestBody UpdateMesaDto updateDto) {
        try {
            Boolean isUpdated = mesaService.update(updateDto);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Mesa actualizada correctamente",
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
            Boolean isDeleted = mesaService.delete(id);
            ApiResponse<Object> response = new ApiResponse<>(
                HttpStatus.OK.value(),
                "Mesa eliminada correctamente",
                false,
                isDeleted
            );
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MesaDto>> findById(@PathVariable Long id) {
        try {
            MesaDto result = mesaService.findById(id);
            ApiResponse<MesaDto> response = new ApiResponse<>(
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
            List<MesaDto> result = mesaService.findAllActive();
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

    @GetMapping("/estado/{estado}")
    public ResponseEntity<ApiResponse<Object>> findByEstado(@PathVariable String estado) {
        try {
            List<MesaDto> result = mesaService.findByEstado(estado);
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
            Page<MesaTableDto> result = mesaService.pageMesas(pageableDto);
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