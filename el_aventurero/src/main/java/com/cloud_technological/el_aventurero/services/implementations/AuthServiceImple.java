package com.cloud_technological.el_aventurero.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cloud_technological.el_aventurero.dto.auth.AuthDto;
import com.cloud_technological.el_aventurero.dto.auth.LoginDto;
import com.cloud_technological.el_aventurero.dto.auth.UserDetailDto;
import com.cloud_technological.el_aventurero.mappers.users.UserMappers;
import com.cloud_technological.el_aventurero.repositories.auth.AuthQueryRepository;
import com.cloud_technological.el_aventurero.repositories.role.RoleQueryRepository;
import com.cloud_technological.el_aventurero.repositories.users.UserJPARepository;
import com.cloud_technological.el_aventurero.repositories.users.UserQueryRepository;
import com.cloud_technological.el_aventurero.security.JwtTokenProvider;
import com.cloud_technological.el_aventurero.services.AuthService;
import com.cloud_technological.el_aventurero.services.UserService;
import com.cloud_technological.el_aventurero.util.AESencryptUtil;
import com.cloud_technological.el_aventurero.util.GlobalException;
import com.cloud_technological.el_aventurero.util.MapperRepository;


@Service
public class AuthServiceImple implements AuthService {

    private static final Integer TIME_TOKEN = 5;


    @Autowired
    private AESencryptUtil encrypt;

    @Autowired
    private AuthQueryRepository authQueryRepository;

    @Autowired
    private MapperRepository mapperRepository;

    @Autowired
    private JwtTokenProvider _jwtTokenProvider;

    private final AuthenticationManager _authenticationManager;

    private final UserService _userService;

    private final UserQueryRepository userQueryRepository;
    private final UserJPARepository userJPARepository;
    private final UserMappers userMappers;
    private final RoleQueryRepository roleQueryRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthServiceImple(AuthenticationManager authenticationManager, UserService userService, JwtTokenProvider jwtTokenProvider,
            UserQueryRepository userQueryRepository, UserJPARepository userJPARepository, UserMappers userMappers, PasswordEncoder passwordEncoder,
            RoleQueryRepository roleQueryRepository
            ) {
        _jwtTokenProvider = jwtTokenProvider;
        this.userQueryRepository = userQueryRepository;
        this.userJPARepository = userJPARepository;
        this.userMappers = userMappers;
        this.roleQueryRepository = roleQueryRepository;
        this.passwordEncoder = passwordEncoder;
        _authenticationManager = authenticationManager;
        _userService = userService;
    }

    public AuthDto login(LoginDto loginDto) {
        try {
            Authentication authentication = _authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Obtener el usuario
            UserDetailDto user = authQueryRepository.findByUserLogin(loginDto.getEmail());
            if (user == null) {
                throw new GlobalException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
            }
            // Generación del token
            String token = _jwtTokenProvider.generateToken(authentication, loginDto.getEmail());
            
            // Crear DTO de respuesta
            AuthDto authDto = new AuthDto();
            authDto.setUser(user);
            authDto.setToken(token);
            
            return authDto;
        } catch (BadCredentialsException e) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        } catch (Exception e) {
            throw new GlobalException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

}
