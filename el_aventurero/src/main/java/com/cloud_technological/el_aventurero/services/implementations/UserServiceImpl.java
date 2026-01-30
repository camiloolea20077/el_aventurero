package com.cloud_technological.el_aventurero.services.implementations;

import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cloud_technological.el_aventurero.dto.users.CreateUserDto;
import com.cloud_technological.el_aventurero.dto.users.UserDto;
import com.cloud_technological.el_aventurero.dto.users.UsersTableDto;
import com.cloud_technological.el_aventurero.entity.UserEntity;
import com.cloud_technological.el_aventurero.mappers.users.UserMappers;
import com.cloud_technological.el_aventurero.repositories.users.UserJPARepository;
import com.cloud_technological.el_aventurero.repositories.users.UserQueryRepository;
import com.cloud_technological.el_aventurero.services.UserService;
import com.cloud_technological.el_aventurero.util.GlobalException;
import com.cloud_technological.el_aventurero.util.PageableDto;


@Service
public class UserServiceImpl implements UserService {

    private final UserQueryRepository userQueryRepository;
    private final UserJPARepository userJPARepository;
    private final UserMappers userMappers;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserQueryRepository userQueryRepository, UserJPARepository userJPARepository,
            UserMappers userMappers, PasswordEncoder passwordEncoder) {
        this.userQueryRepository = userQueryRepository;
        this.userJPARepository = userJPARepository;
        this.userMappers = userMappers;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto create(CreateUserDto createUserDto) {
        Boolean exists = userQueryRepository.existsByEmail(createUserDto.getEmail().toLowerCase());
        if (exists)
            throw new GlobalException(HttpStatus.BAD_REQUEST, "El correo ya se encuentra registrado");

        try {
            String encodedPassword = passwordEncoder.encode(createUserDto.getPassword());
            createUserDto.setPassword(encodedPassword);
            UserEntity userEntity = userMappers
                    .createToEntity(createUserDto);
            System.out.println("Guardando usuario: " + userEntity.toString());
            UserEntity saveUserEntity = userJPARepository
                    .save(userEntity);

            return userMappers.toDto(saveUserEntity);
        } catch (Exception e) {
            System.err.println("Error al crear el usuario: " + e.toString());
            e.printStackTrace();
            throw new RuntimeException("Error al crear el usuario: " + e.getMessage(), e);
        }
    }
    @Override
    public PageImpl<UsersTableDto> pageUsers(PageableDto<Object> pageableDto) {
        return userQueryRepository.listUsers(pageableDto);
    }
}
