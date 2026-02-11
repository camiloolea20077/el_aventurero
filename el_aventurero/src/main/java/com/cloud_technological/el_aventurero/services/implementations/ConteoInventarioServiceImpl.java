package com.cloud_technological.el_aventurero.services.implementations;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud_technological.el_aventurero.dto.conteo_inventario.AjusteInventarioDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.ConteoInventarioDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.CreateAjusteDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.CreateConteoDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.CreateDetalleConteoDto;
import com.cloud_technological.el_aventurero.dto.conteo_inventario.DetalleConteoDto;
import com.cloud_technological.el_aventurero.entity.AjusteInventarioEntity;
import com.cloud_technological.el_aventurero.entity.ConteoInventarioEntity;
import com.cloud_technological.el_aventurero.entity.DetalleConteoEntity;
import com.cloud_technological.el_aventurero.entity.InventarioEntity;
import com.cloud_technological.el_aventurero.mappers.conteo_inventario.AjusteInventarioMapper;
import com.cloud_technological.el_aventurero.mappers.conteo_inventario.ConteoInventarioMapper;
import com.cloud_technological.el_aventurero.mappers.conteo_inventario.DetalleConteoMapper;
import com.cloud_technological.el_aventurero.repositories.ajuste_inventario.AjusteInventarioJPARepository;
import com.cloud_technological.el_aventurero.repositories.ajuste_inventario.AjusteInventarioQueryRepository;
import com.cloud_technological.el_aventurero.repositories.conteo_inventario.ConteoInventarioJPARepository;
import com.cloud_technological.el_aventurero.repositories.conteo_inventario.ConteoInventarioQueryRepository;
import com.cloud_technological.el_aventurero.repositories.detalle_conteo.DetalleConteoJPARepository;
import com.cloud_technological.el_aventurero.repositories.detalle_conteo.DetalleConteoQueryRepository;
import com.cloud_technological.el_aventurero.repositories.inventario.InventarioJPARepository;
import com.cloud_technological.el_aventurero.services.ConteoInventarioService;
import com.cloud_technological.el_aventurero.util.GlobalException;

@Service
public class ConteoInventarioServiceImpl implements ConteoInventarioService {

    private final ConteoInventarioJPARepository conteoJPARepository;
    private final ConteoInventarioQueryRepository conteoQueryRepository;
    private final ConteoInventarioMapper conteoMapper;

    private final DetalleConteoJPARepository detalleJPARepository;
    private final DetalleConteoQueryRepository detalleQueryRepository;
    private final DetalleConteoMapper detalleMapper;

    private final AjusteInventarioJPARepository ajusteJPARepository;
    private final AjusteInventarioQueryRepository ajusteQueryRepository;
    private final AjusteInventarioMapper ajusteMapper;

    private final InventarioJPARepository inventarioJPARepository;

    public ConteoInventarioServiceImpl(
            ConteoInventarioJPARepository conteoJPARepository,
            ConteoInventarioQueryRepository conteoQueryRepository,
            ConteoInventarioMapper conteoMapper,
            DetalleConteoJPARepository detalleJPARepository,
            DetalleConteoQueryRepository detalleQueryRepository,
            DetalleConteoMapper detalleMapper,
            AjusteInventarioJPARepository ajusteJPARepository,
            AjusteInventarioQueryRepository ajusteQueryRepository,
            AjusteInventarioMapper ajusteMapper,
            InventarioJPARepository inventarioJPARepository) {
        this.conteoJPARepository = conteoJPARepository;
        this.conteoQueryRepository = conteoQueryRepository;
        this.conteoMapper = conteoMapper;
        this.detalleJPARepository = detalleJPARepository;
        this.detalleQueryRepository = detalleQueryRepository;
        this.detalleMapper = detalleMapper;
        this.ajusteJPARepository = ajusteJPARepository;
        this.ajusteQueryRepository = ajusteQueryRepository;
        this.ajusteMapper = ajusteMapper;
        this.inventarioJPARepository = inventarioJPARepository;
    }

    // ==================== CONTEO ====================

    @Override
    @Transactional
    public ConteoInventarioDto iniciarConteo(CreateConteoDto createDto) {
        try {
            ConteoInventarioEntity entity = conteoMapper.createToEntity(createDto);
            ConteoInventarioEntity savedEntity = conteoJPARepository.save(entity);
            return conteoQueryRepository.findById(savedEntity.getId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al iniciar conteo de inventario: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ConteoInventarioDto completarConteo(Long conteoId) {
        ConteoInventarioEntity entity = conteoJPARepository.findById(conteoId)
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Conteo no encontrado"));

        // Validar que no haya detalles pendientes de ajustar
        List<DetalleConteoDto> pendientes = detalleQueryRepository.findPendientesAjuste(conteoId);
        if (!pendientes.isEmpty()) {
            throw new GlobalException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede completar el conteo. Hay " + pendientes.size()
                            + " productos con diferencias sin ajustar");
        }

        try {
            entity.setEstado("COMPLETADO");
            conteoJPARepository.save(entity);
            return conteoQueryRepository.findById(conteoId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al completar conteo: " + e.getMessage(), e);
        }
    }

    @Override
    public ConteoInventarioDto findById(Long id) {
        ConteoInventarioDto conteo = conteoQueryRepository.findById(id);
        if (conteo == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Conteo no encontrado");
        }
        return conteo;
    }

    @Override
    public List<ConteoInventarioDto> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        return conteoQueryRepository.findByFechaBetween(fechaInicio, fechaFin);
    }

    @Override
    public ConteoInventarioDto getLastConteo() {
        return conteoQueryRepository.findLastConteo();
    }

    // ==================== DETALLES ====================

    @Override
    @Transactional
    public DetalleConteoDto registrarDetalle(CreateDetalleConteoDto createDto) {
        // Validar que el conteo existe
        ConteoInventarioEntity conteo = conteoJPARepository.findById(createDto.getConteo_id())
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Conteo no encontrado"));

        // Validar que el conteo no esté completado
        if ("COMPLETADO".equals(conteo.getEstado())) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "No se pueden agregar detalles a un conteo completado");
        }

        try {
            // ✅ BUSCAR SI YA EXISTE UN DETALLE PARA ESTE PRODUCTO EN ESTE CONTEO
            List<DetalleConteoDto> detallesExistentes = detalleQueryRepository.findByConteoId(createDto.getConteo_id());
            
            for (DetalleConteoDto detalleExistente : detallesExistentes) {
                if (detalleExistente.getProducto_id().equals(createDto.getProducto_id())) {
                    // ✅ SI YA EXISTE: Actualizar solo stock_fisico, MANTENER stock_sistema original
                    DetalleConteoEntity entityExistente = detalleJPARepository.findById(detalleExistente.getId())
                            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Detalle no encontrado"));
                    
                    // MANTENER el stock_sistema que se guardó originalmente
                    // Solo actualizar stock_fisico y recalcular diferencia
                    entityExistente.setStock_fisico(createDto.getStock_fisico());
                    entityExistente.setDiferencia(createDto.getStock_fisico() - entityExistente.getStock_sistema());
                    
                    DetalleConteoEntity savedEntity = detalleJPARepository.save(entityExistente);
                    return detalleQueryRepository.findById(savedEntity.getId());
                }
            }

            // ✅ SI NO EXISTE: Crear nuevo con stock_sistema del inventario ACTUAL
            InventarioEntity inventario = inventarioJPARepository.findById(createDto.getProducto_id())
                    .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Producto no encontrado en inventario"));

            DetalleConteoEntity entity = detalleMapper.createToEntity(createDto);
            entity.setStock_sistema(inventario.getStock());
            entity.setDiferencia(createDto.getStock_fisico() - inventario.getStock());

            DetalleConteoEntity savedEntity = detalleJPARepository.save(entity);
            return detalleQueryRepository.findById(savedEntity.getId());
            
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al registrar detalle de conteo: " + e.getMessage(), e);
        }
    }
    @Override
    public List<DetalleConteoDto> getDetallesByConteoId(Long conteoId) {
        return detalleQueryRepository.findByConteoId(conteoId);
    }

    @Override
    public List<DetalleConteoDto> getDiferencias(Long conteoId) {
        return detalleQueryRepository.findDiferencias(conteoId);
    }

    @Override
    public List<DetalleConteoDto> getPendientesAjuste(Long conteoId) {
        return detalleQueryRepository.findPendientesAjuste(conteoId);
    }

    // ==================== AJUSTES ====================

    @Override
    @Transactional
    public AjusteInventarioDto ajustarInventario(CreateAjusteDto createDto) {
        // Validar que el producto existe en inventario
        InventarioEntity inventario = inventarioJPARepository.findById(createDto.getProducto_id())
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Producto no encontrado en inventario"));

        try {
            // 1. Guardar el ajuste
            AjusteInventarioEntity ajusteEntity = ajusteMapper.createToEntity(createDto);
            AjusteInventarioEntity savedAjuste = ajusteJPARepository.save(ajusteEntity);

            // 2. Actualizar el stock en inventario
            if ("SUMA".equals(createDto.getTipo())) {
                inventario.setStock(inventario.getStock() + createDto.getCantidad());
            } else if ("RESTA".equals(createDto.getTipo())) {
                int nuevoStock = inventario.getStock() - createDto.getCantidad();
                if (nuevoStock < 0) {
                    throw new GlobalException(
                            HttpStatus.BAD_REQUEST,
                            "No se puede restar " + createDto.getCantidad() + " unidades. Stock actual: "
                                    + inventario.getStock());
                }
                inventario.setStock(nuevoStock);
            } else {
                throw new GlobalException(HttpStatus.BAD_REQUEST, "Tipo de ajuste inválido. Use SUMA o RESTA");
            }
            inventarioJPARepository.save(inventario);

            // 3. Si el ajuste viene de un conteo, marcar el detalle como ajustado
            if (createDto.getConteo_id() != null) {
                // Buscar el detalle del conteo para este producto
                List<DetalleConteoDto> detalles = detalleQueryRepository.findByConteoId(createDto.getConteo_id());
                for (DetalleConteoDto detalle : detalles) {
                    if (detalle.getProducto_id().equals(createDto.getProducto_id())) {
                        DetalleConteoEntity detalleEntity = detalleJPARepository.findById(detalle.getId())
                                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND,
                                        "Detalle de conteo no encontrado"));
                        detalleEntity.setAjustado(true);
                        detalleEntity.setMotivo(createDto.getMotivo());
                        detalleJPARepository.save(detalleEntity);
                        break;
                    }
                }
            }

            return ajusteQueryRepository.findById(savedAjuste.getId());
        } catch (GlobalException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al ajustar inventario: " + e.getMessage(), e);
        }
    }

    @Override
    public List<AjusteInventarioDto> getAjustesByConteoId(Long conteoId) {
        return ajusteQueryRepository.findByConteoId(conteoId);
    }

    @Override
    public List<AjusteInventarioDto> getAjustesByProductoId(Long productoId) {
        return ajusteQueryRepository.findByProductoId(productoId);
    }
}