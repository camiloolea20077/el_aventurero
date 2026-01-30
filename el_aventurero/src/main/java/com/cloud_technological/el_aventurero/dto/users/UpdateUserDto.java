package com.cloud_technological.el_aventurero.dto.users;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserDto {
    private Long id;
    private String name;
    private String email;
    private Long rol_id;
    private String password;
    private List<String> permisos;
}
