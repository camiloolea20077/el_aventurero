package com.cloud_technological.el_aventurero.services.implementations;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud_technological.el_aventurero.dto.roles.CreateRoleDto;
import com.cloud_technological.el_aventurero.dto.roles.RoleDto;
import com.cloud_technological.el_aventurero.dto.roles.UpdateRoleDto;
import com.cloud_technological.el_aventurero.entity.RoleEntity;
import com.cloud_technological.el_aventurero.mappers.roles.RolesMapper;
import com.cloud_technological.el_aventurero.repositories.role.RoleJPARepository;
import com.cloud_technological.el_aventurero.repositories.role.RoleQueryRepository;
import com.cloud_technological.el_aventurero.services.RoleService;
import com.cloud_technological.el_aventurero.util.GlobalException;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleJPARepository roleJPARepository;
    private final RoleQueryRepository roleQueryRepository;
    private final RolesMapper rolesMapper;

    public RoleServiceImpl(
        RoleJPARepository roleJPARepository,
        RoleQueryRepository roleQueryRepository,
        RolesMapper rolesMapper
    ) {
        this.roleJPARepository = roleJPARepository;
        this.roleQueryRepository = roleQueryRepository;
        this.rolesMapper = rolesMapper;
    }

    @Override
    @Transactional
    public RoleDto create(CreateRoleDto createDto) {
        if (createDto.getName() == null || createDto.getName().trim().isEmpty()) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Nombre es requerido");
        }

        String nombre = createDto.getName().trim();

        if (createDto.getActive() != null && createDto.getActive() != 1L && createDto.getActive() != 2L) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Activo debe ser 1 o 2");
        }

        Boolean exists = roleQueryRepository.existsByNombre(nombre);
        if (exists) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un rol con ese nombre");
        }

        try {
            RoleEntity entity = rolesMapper.createToEntity(createDto);
            entity.setNombre(nombre);
            if (entity.getActivo() == null) {
                entity.setActivo(1L);
            }

            RoleEntity saved = roleJPARepository.save(entity);
            return rolesMapper.toDto(saved);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear el rol: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Boolean update(UpdateRoleDto updateDto) {
        if (updateDto.getId() == null) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Id es requerido");
        }

        RoleEntity entity = roleJPARepository.findById(updateDto.getId())
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        if (entity.getDeleted_at() != null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Rol no encontrado");
        }

        if (updateDto.getName() != null) {
            String nombre = updateDto.getName().trim();
            if (nombre.isEmpty()) {
                throw new GlobalException(HttpStatus.BAD_REQUEST, "Nombre es requerido");
            }

            if (!nombre.equalsIgnoreCase(entity.getNombre())) {
                Boolean exists = roleQueryRepository.existsByNombreExcludingId(nombre, updateDto.getId());
                if (exists) {
                    throw new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un rol con ese nombre");
                }
            }

            entity.setNombre(nombre);
        }

        if (updateDto.getDescription() != null) {
            entity.setDescripcion(updateDto.getDescription());
        }

        if (updateDto.getActive() != null) {
            if (updateDto.getActive() != 1L && updateDto.getActive() != 2L) {
                throw new GlobalException(HttpStatus.BAD_REQUEST, "Activo debe ser 1 o 2");
            }
            entity.setActivo(updateDto.getActive());
        }

        try {
            roleJPARepository.save(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar el rol");
        }
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        RoleEntity entity = roleJPARepository.findById(id)
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Rol no encontrado"));

        if (entity.getDeleted_at() != null) {
            return true;
        }

        try {
            entity.setDeleted_at(LocalDateTime.now());
            entity.setActivo(2L);
            roleJPARepository.save(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar el rol");
        }
    }

    @Override
    public RoleDto findById(Long id) {
        RoleDto dto = roleQueryRepository.findById(id);
        if (dto == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Rol no encontrado");
        }
        return dto;
    }

    @Override
    public List<RoleDto> findAll() {
        return roleQueryRepository.findAll();
    }

    @Override
    public List<RoleDto> findAllActive() {
        return roleQueryRepository.findAllActive();
    }
}
