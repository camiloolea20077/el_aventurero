package com.cloud_technological.el_aventurero.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cloud_technological.el_aventurero.dto.auth.AuthDto;
import com.cloud_technological.el_aventurero.dto.auth.LoginDto;
import com.cloud_technological.el_aventurero.services.AuthService;
import com.cloud_technological.el_aventurero.util.ApiResponse;


@RestController
@RequestMapping("/security")
public class AuthenticationController {
    private static final String VERSION_AUTH = "3.1.0";

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody @Valid LoginDto loginDto) throws Exception {
        try {
            AuthDto authData = authService.login(loginDto);
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.OK.value(), "Credenciales correctas", false,
                    authData);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            throw ex;
        }
    }
}
