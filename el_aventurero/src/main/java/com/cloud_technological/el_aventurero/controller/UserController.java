package com.cloud_technological.el_aventurero.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_technological.el_aventurero.dto.users.CreateUserDto;
import com.cloud_technological.el_aventurero.dto.users.UserDto;
import com.cloud_technological.el_aventurero.dto.users.UsersTableDto;
import com.cloud_technological.el_aventurero.services.UserService;
import com.cloud_technological.el_aventurero.util.ApiResponse;
import com.cloud_technological.el_aventurero.util.GlobalException;
import com.cloud_technological.el_aventurero.util.PageableDto;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Object>> createUserDataBasic(
        @Valid @RequestBody CreateUserDto createUserDto) throws Exception {
        try {
            UserDto savedUser = userService
                .create(createUserDto);
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.CREATED.value(),
                "Registro creado exitosamente", false, savedUser);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }
    @PostMapping("/page")
    public ResponseEntity<ApiResponse<Object>> listUsers(
            @Valid @RequestBody PageableDto<Object> pageableDto) {
        try {
            Page<UsersTableDto> convention = this.userService.pageUsers(pageableDto);
            if (convention.isEmpty())
                throw new GlobalException(HttpStatus.PARTIAL_CONTENT, "No se encontraron registros");
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "", false, convention);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }
}
