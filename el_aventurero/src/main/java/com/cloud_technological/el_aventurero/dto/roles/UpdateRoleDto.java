package com.cloud_technological.el_aventurero.dto.roles;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRoleDto {
    private Long id;
    private String description;
    private String name;
    private Long active;
}
