package com.cloud_technological.el_aventurero.dto.roles;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoleDto {
    private String name;
    private String description;
    private Long active;
}
