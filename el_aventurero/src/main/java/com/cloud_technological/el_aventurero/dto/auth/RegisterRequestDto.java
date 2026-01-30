package com.cloud_technological.el_aventurero.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {
    // Empresa
    private String company_name;
    private String nit;
    private String email;
    private String address;
    private String phone;
    private String plan;
    private Long farm_id;
    // Usuario administrador
    private String full_name;
    private String admin_email;
    private String password;
    private String username;
}
