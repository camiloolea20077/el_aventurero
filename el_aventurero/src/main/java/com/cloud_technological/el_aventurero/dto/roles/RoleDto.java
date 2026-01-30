package com.cloud_technological.el_aventurero.dto.roles;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDto {
    private Long id;
    private String name;
    private String description;
    private Long active;
}
