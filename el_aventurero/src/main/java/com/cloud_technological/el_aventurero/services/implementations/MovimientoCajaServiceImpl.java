package com.cloud_technological.el_aventurero.services.implementations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud_technological.el_aventurero.dto.movimiento_caja.CierreSemanalDto;
import com.cloud_technological.el_aventurero.dto.movimiento_caja.CreateMovimientoDto;
import com.cloud_technological.el_aventurero.dto.movimiento_caja.MovimientoCajaDto;
import com.cloud_technological.el_aventurero.dto.movimiento_caja.ResumenArqueosDto;
import com.cloud_technological.el_aventurero.dto.movimiento_caja.ResumenFlujoDto;
import com.cloud_technological.el_aventurero.entity.MovimientoCajaEntity;
import com.cloud_technological.el_aventurero.mappers.movimiento_caja.MovimientoCajaMapper;
import com.cloud_technological.el_aventurero.repositories.movimiento_caja.MovimientoCajaJPARepository;
import com.cloud_technological.el_aventurero.repositories.movimiento_caja.MovimientoCajaQueryRepository;
import com.cloud_technological.el_aventurero.services.MovimientoCajaService;
import com.cloud_technological.el_aventurero.util.GlobalException;

@Service
public class MovimientoCajaServiceImpl implements MovimientoCajaService {

    private final MovimientoCajaJPARepository movimientoCajaJPARepository;
    private final MovimientoCajaQueryRepository movimientoCajaQueryRepository;
    private final MovimientoCajaMapper movimientoCajaMapper;

    public MovimientoCajaServiceImpl(
        MovimientoCajaJPARepository movimientoCajaJPARepository,
        MovimientoCajaQueryRepository movimientoCajaQueryRepository,
        MovimientoCajaMapper movimientoCajaMapper
    ) {
        this.movimientoCajaJPARepository = movimientoCajaJPARepository;
        this.movimientoCajaQueryRepository = movimientoCajaQueryRepository;
        this.movimientoCajaMapper = movimientoCajaMapper;
    }

    @Override
    @Transactional
    public MovimientoCajaDto create(CreateMovimientoDto createDto) {
        // Validar tipo
        if (!createDto.getTipo().equals("INGRESO") && !createDto.getTipo().equals("EGRESO")) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Tipo de movimiento inválido");
        }

        try {
            // Usar el mapper para convertir DTO a Entity
            MovimientoCajaEntity entity = movimientoCajaMapper.createToEntity(createDto);
            
            // Guardar la entidad
            MovimientoCajaEntity savedEntity = movimientoCajaJPARepository.save(entity);
            
            // Retornar el DTO desde la base de datos
            return movimientoCajaQueryRepository.findById(savedEntity.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear el movimiento: " + e.getMessage(), e);
        }
    }
    @Override
    @Transactional
    public Boolean delete(Long id) {
        MovimientoCajaEntity entity = movimientoCajaJPARepository.findById(id)
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Movimiento no encontrado"));

        try {
            entity.setDeleted_at(LocalDateTime.now());
            entity.setActivo(2L);
            movimientoCajaJPARepository.save(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar el movimiento");
        }
    }

    @Override
    public MovimientoCajaDto findById(Long id) {
        MovimientoCajaDto dto = movimientoCajaQueryRepository.findById(id);
        if (dto == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Movimiento no encontrado");
        }
        return dto;
    }

    @Override
    public List<MovimientoCajaDto> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        return movimientoCajaQueryRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    @Override
    public ResumenFlujoDto getResumenFlujo(LocalDate fechaInicio, LocalDate fechaFin) {
        return movimientoCajaQueryRepository.getResumenFlujo(fechaInicio, fechaFin);
    }
    
    @Override
    public CierreSemanalDto getCierreSemanal(LocalDate fechaInicio, LocalDate fechaFin) {
        CierreSemanalDto cierre = movimientoCajaQueryRepository.getCierreSemanal(fechaInicio, fechaFin);
        ResumenArqueosDto resumenArqueos = movimientoCajaQueryRepository.getResumenArqueos(fechaInicio, fechaFin);
        cierre.setResumen_arqueos(resumenArqueos);
        
        return cierre;
    }
}