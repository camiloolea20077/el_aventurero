package com.cloud_technological.el_aventurero.services.implementations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud_technological.el_aventurero.dto.mesas.CreateMesaDto;
import com.cloud_technological.el_aventurero.dto.mesas.MesaDto;
import com.cloud_technological.el_aventurero.dto.mesas.MesaTableDto;
import com.cloud_technological.el_aventurero.dto.mesas.UpdateMesaDto;
import com.cloud_technological.el_aventurero.entity.MesaEntity;
import com.cloud_technological.el_aventurero.mappers.mesas.MesaMapper;
import com.cloud_technological.el_aventurero.repositories.mesas.MesaJPARepository;
import com.cloud_technological.el_aventurero.repositories.mesas.MesaQueryRepository;
import com.cloud_technological.el_aventurero.services.MesaService;
import com.cloud_technological.el_aventurero.util.GlobalException;
import com.cloud_technological.el_aventurero.util.PageableDto;

@Service
public class MesaServiceImpl implements MesaService {

    private final MesaJPARepository mesaJPARepository;
    private final MesaQueryRepository mesaQueryRepository;
    private final MesaMapper mesaMapper;

    public MesaServiceImpl(
        MesaJPARepository mesaJPARepository,
        MesaQueryRepository mesaQueryRepository,
        MesaMapper mesaMapper
    ) {
        this.mesaJPARepository = mesaJPARepository;
        this.mesaQueryRepository = mesaQueryRepository;
        this.mesaMapper = mesaMapper;
    }

    @Override
    @Transactional
    public MesaDto create(CreateMesaDto createDto) {
        // Validar estado
        if (!createDto.getEstado().equals("LIBRE") && !createDto.getEstado().equals("OCUPADA")) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Estado debe ser LIBRE u OCUPADA");
        }

        // Verificar que no exista mesa con ese número
        Boolean exists = mesaQueryRepository.existsByNumero(createDto.getNumero());
        if (exists) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una mesa con ese número");
        }

        try {
            MesaEntity entity = mesaMapper.createToEntity(createDto);
            entity.setTotal_acumulado(BigDecimal.ZERO);
            MesaEntity savedEntity = mesaJPARepository.save(entity);
            return mesaMapper.toDto(savedEntity);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear la mesa: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Boolean update(UpdateMesaDto updateDto) {
        MesaEntity entity = mesaJPARepository.findById(updateDto.getId())
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));

        // Validar estado si se actualiza
        if (updateDto.getEstado() != null) {
            if (!updateDto.getEstado().equals("LIBRE") && !updateDto.getEstado().equals("OCUPADA")) {
                throw new GlobalException(HttpStatus.BAD_REQUEST, "Estado debe ser LIBRE u OCUPADA");
            }
        }

        // Validar número único si se actualiza
        if (updateDto.getNumero() != null && !updateDto.getNumero().equals(entity.getNumero())) {
            Boolean exists = mesaQueryRepository.existsByNumeroExcludingId(updateDto.getNumero(), updateDto.getId());
            if (exists) {
                throw new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe una mesa con ese número");
            }
        }

        try {
            mesaMapper.updateEntityFromDto(updateDto, entity);
            mesaJPARepository.save(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar la mesa");
        }
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        MesaEntity entity = mesaJPARepository.findById(id)
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));

        // No permitir eliminar mesa ocupada
        if (entity.getEstado().equals("OCUPADA")) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "No se puede eliminar una mesa ocupada");
        }

        try {
            entity.setDeleted_at(LocalDateTime.now());
            entity.setActivo(2L);
            mesaJPARepository.save(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar la mesa");
        }
    }

    @Override
    public MesaDto findById(Long id) {
        MesaEntity entity = mesaJPARepository.findById(id)
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
        
        return mesaMapper.toDto(entity);
    }

    @Override
    public List<MesaDto> findAllActive() {
        return mesaQueryRepository.findAllActive();
    }

    @Override
    public List<MesaDto> findByEstado(String estado) {
        if (!estado.equals("LIBRE") && !estado.equals("OCUPADA")) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Estado debe ser LIBRE u OCUPADA");
        }
        return mesaQueryRepository.findByEstado(estado);
    }

    @Override
    public Page<MesaTableDto> pageMesas(PageableDto<Object> pageableDto) {
        return mesaQueryRepository.listMesas(pageableDto);
    }
}