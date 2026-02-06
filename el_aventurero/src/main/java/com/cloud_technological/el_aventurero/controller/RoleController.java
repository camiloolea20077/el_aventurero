package com.cloud_technological.el_aventurero.controller;

import java.util.List;

import javax.validation.Valid;

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

import com.cloud_technological.el_aventurero.dto.roles.CreateRoleDto;
import com.cloud_technological.el_aventurero.dto.roles.RoleDto;
import com.cloud_technological.el_aventurero.dto.roles.UpdateRoleDto;
import com.cloud_technological.el_aventurero.services.RoleService;
import com.cloud_technological.el_aventurero.util.ApiResponse;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<RoleDto>> create(@Valid @RequestBody CreateRoleDto createDto) {
        RoleDto result = roleService.create(createDto);
        ApiResponse<RoleDto> response = new ApiResponse<>(
            HttpStatus.CREATED.value(),
            "Rol creado correctamente",
            false,
            result
        );
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<Object>> update(@Valid @RequestBody UpdateRoleDto updateDto) {
        Boolean isUpdated = roleService.update(updateDto);
        ApiResponse<Object> response = new ApiResponse<>(
            HttpStatus.OK.value(),
            "Rol actualizado correctamente",
            false,
            isUpdated
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable("id") Long id) {
        Boolean isDeleted = roleService.delete(id);
        ApiResponse<Object> response = new ApiResponse<>(
            HttpStatus.OK.value(),
            "Rol eliminado correctamente",
            false,
            isDeleted
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoleDto>> findById(@PathVariable Long id) {
        RoleDto result = roleService.findById(id);
        ApiResponse<RoleDto> response = new ApiResponse<>(
            HttpStatus.OK.value(),
            "Elemento encontrado",
            false,
            result
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<Object>> listActive() {
        List<RoleDto> result = roleService.findAllActive();
        ApiResponse<Object> response = new ApiResponse<>(
            HttpStatus.OK.value(),
            "",
            false,
            result
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list-all")
    public ResponseEntity<ApiResponse<Object>> listAll() {
        List<RoleDto> result = roleService.findAll();
        ApiResponse<Object> response = new ApiResponse<>(
            HttpStatus.OK.value(),
            "",
            false,
            result
        );
        return ResponseEntity.ok(response);
    }
}
