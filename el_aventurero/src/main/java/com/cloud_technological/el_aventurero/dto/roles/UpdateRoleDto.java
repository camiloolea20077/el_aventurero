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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getActive() {
        return active;
    }

    public void setActive(Long active) {
        this.active = active;
    }
}
