package com.cloud_technological.el_aventurero.services.implementations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud_technological.el_aventurero.dto.arqueo_caja.ArqueoCajaDto;
import com.cloud_technological.el_aventurero.dto.arqueo_caja.CreateArqueoDto;
import com.cloud_technological.el_aventurero.dto.arqueo_caja.DatosParaArqueoDto;
import com.cloud_technological.el_aventurero.dto.arqueo_caja.UpdateEstadoArqueoDto;
import com.cloud_technological.el_aventurero.entity.ArqueoCajaEntity;
import com.cloud_technological.el_aventurero.mappers.arqueo_caja.ArqueoCajaMapper;
import com.cloud_technological.el_aventurero.repositories.arqueo_caja.ArqueoCajaJPARepository;
import com.cloud_technological.el_aventurero.repositories.arqueo_caja.ArqueoCajaQueryRepository;
import com.cloud_technological.el_aventurero.services.ArqueoCajaService;
import com.cloud_technological.el_aventurero.util.GlobalException;

@Service
public class ArqueoCajaServiceImpl implements ArqueoCajaService {

    private final ArqueoCajaJPARepository arqueoCajaJPARepository;
    private final ArqueoCajaQueryRepository arqueoCajaQueryRepository;
    private final ArqueoCajaMapper arqueoCajaMapper;

    public ArqueoCajaServiceImpl(
        ArqueoCajaJPARepository arqueoCajaJPARepository,
        ArqueoCajaQueryRepository arqueoCajaQueryRepository,
        ArqueoCajaMapper arqueoCajaMapper
    ) {
        this.arqueoCajaJPARepository = arqueoCajaJPARepository;
        this.arqueoCajaQueryRepository = arqueoCajaQueryRepository;
        this.arqueoCajaMapper = arqueoCajaMapper;
    }

    @Override
    @Transactional
    public ArqueoCajaDto create(CreateArqueoDto createDto) {
        // Validar que no exista un arqueo para la fecha
        ArqueoCajaDto arqueoExistente = arqueoCajaQueryRepository.findByFecha(createDto.getFecha());
        if (arqueoExistente != null) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un arqueo para esta fecha");
        }

        try {
            // Obtener datos del sistema para la fecha
            DatosParaArqueoDto datosParaArqueo = arqueoCajaQueryRepository.getDatosParaArqueo(createDto.getFecha());

            // Usar mapper para convertir DTO a Entity
            ArqueoCajaEntity entity = arqueoCajaMapper.createToEntity(createDto);

            // Establecer datos del sistema
            entity.setTotal_ingresos_sistema(datosParaArqueo.getTotal_ingresos());
            entity.setTotal_egresos_sistema(datosParaArqueo.getTotal_egresos());
            entity.setSaldo_esperado(datosParaArqueo.getSaldo_esperado());

            // Calcular diferencia
            BigDecimal diferencia = createDto.getEfectivo_real().subtract(datosParaArqueo.getSaldo_esperado());
            entity.setDiferencia(diferencia);

            // Determinar estado
            String estado;
            if (diferencia.compareTo(BigDecimal.ZERO) == 0) {
                estado = "CUADRADO";
            } else if (Math.abs(diferencia.doubleValue()) <= 10000) {
                estado = "PENDIENTE";
            } else {
                estado = "PENDIENTE";
            }
            entity.setEstado(estado);

            // Guardar
            ArqueoCajaEntity savedEntity = arqueoCajaJPARepository.save(entity);

            // Retornar DTO desde Query Repository
            return arqueoCajaQueryRepository.findById(savedEntity.getId());
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear el arqueo: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        ArqueoCajaEntity entity = arqueoCajaJPARepository.findById(id)
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Arqueo no encontrado"));

        try {
            entity.setDeleted_at(LocalDateTime.now());
            entity.setActivo(2L);
            arqueoCajaJPARepository.save(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar el arqueo");
        }
    }

    @Override
    public ArqueoCajaDto findById(Long id) {
        ArqueoCajaDto dto = arqueoCajaQueryRepository.findById(id);
        if (dto == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Arqueo no encontrado");
        }
        return dto;
    }

    @Override
    public List<ArqueoCajaDto> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        return arqueoCajaQueryRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    @Override
    public ArqueoCajaDto findByFecha(LocalDate fecha) {
        return arqueoCajaQueryRepository.findByFecha(fecha);
    }

    @Override
    public DatosParaArqueoDto getDatosParaArqueo(LocalDate fecha) {
        return arqueoCajaQueryRepository.getDatosParaArqueo(fecha);
    }
    
    @Override
    @Transactional
    public ArqueoCajaDto updateEstado(Long id, UpdateEstadoArqueoDto updateDto) {
        ArqueoCajaEntity entity = arqueoCajaJPARepository.findById(id)
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Arqueo no encontrado"));

        // Validar que solo se pueda cambiar a AJUSTADO
        if (!"AJUSTADO".equals(updateDto.getEstado())) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Solo se puede cambiar el estado a AJUSTADO");
        }

        // Validar que tenga observaciones
        if (updateDto.getObservaciones() == null || updateDto.getObservaciones().trim().isEmpty()) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Debe proporcionar observaciones para el ajuste");
        }

        try {
            entity.setEstado(updateDto.getEstado());
            entity.setObservaciones(updateDto.getObservaciones());
            arqueoCajaJPARepository.save(entity);

            // Retornar DTO actualizado
            return arqueoCajaQueryRepository.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar el estado del arqueo");
        }
    }
}