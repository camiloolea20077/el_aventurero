package com.cloud_technological.el_aventurero.dto.users;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Long rol_id;
    private Long active;
    private List<String> permisos;
}
