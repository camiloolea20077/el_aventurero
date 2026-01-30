package com.cloud_technological.el_aventurero.services;

import com.cloud_technological.el_aventurero.dto.auth.AuthDto;
import com.cloud_technological.el_aventurero.dto.auth.LoginDto;

public interface AuthService {
    public AuthDto login(LoginDto loginDto);
}
