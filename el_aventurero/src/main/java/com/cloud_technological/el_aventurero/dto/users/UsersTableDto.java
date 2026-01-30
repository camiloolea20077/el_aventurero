package com.cloud_technological.el_aventurero.dto.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsersTableDto {
    private Long id;
    private String name;
    private String email;
    private String role;
    private Long activo;
}
