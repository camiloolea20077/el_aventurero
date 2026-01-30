package com.cloud_technological.el_aventurero.services;

import org.springframework.data.domain.PageImpl;

import com.cloud_technological.el_aventurero.dto.users.CreateUserDto;
import com.cloud_technological.el_aventurero.dto.users.UserDto;
import com.cloud_technological.el_aventurero.dto.users.UsersTableDto;
import com.cloud_technological.el_aventurero.util.PageableDto;



public interface UserService {
    UserDto create(CreateUserDto createUserDto);
    PageImpl<UsersTableDto> pageUsers(PageableDto<Object> pageableDto);
    
}
