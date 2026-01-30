package com.cloud_technological.el_aventurero.dto.auth;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailDto {
    private Integer id;
    private String name;
    private String email;
    private String role;
    private List<String> permisos; 
}
