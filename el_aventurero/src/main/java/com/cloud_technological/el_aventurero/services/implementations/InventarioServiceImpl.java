package com.cloud_technological.el_aventurero.services.implementations;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.naming.java.javaURLContextFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud_technological.el_aventurero.dto.inventario.AjusteStockDto;
import com.cloud_technological.el_aventurero.dto.inventario.CreateInventarioDto;
import com.cloud_technological.el_aventurero.dto.inventario.InventarioDto;
import com.cloud_technological.el_aventurero.dto.inventario.InventarioTableDto;
import com.cloud_technological.el_aventurero.dto.inventario.UpdateInventarioDto;
import com.cloud_technological.el_aventurero.entity.InventarioEntity;
import com.cloud_technological.el_aventurero.mappers.inventario.InventarioMapper;
import com.cloud_technological.el_aventurero.repositories.inventario.InventarioJPARepository;
import com.cloud_technological.el_aventurero.repositories.inventario.InventarioQueryRepository;
import com.cloud_technological.el_aventurero.repositories.productos.ProductoJPARepository;
import com.cloud_technological.el_aventurero.services.InventarioService;
import com.cloud_technological.el_aventurero.util.GlobalException;
import com.cloud_technological.el_aventurero.util.PageableDto;

@Service
public class InventarioServiceImpl implements InventarioService {

    private final InventarioJPARepository inventarioJPARepository;
    private final InventarioQueryRepository inventarioQueryRepository;
    private final ProductoJPARepository productoJPARepository;
    private final InventarioMapper inventarioMapper;

    public InventarioServiceImpl(
        InventarioJPARepository inventarioJPARepository,
        InventarioQueryRepository inventarioQueryRepository,
        ProductoJPARepository productoJPARepository,
        InventarioMapper inventarioMapper
    ) {
        this.inventarioJPARepository = inventarioJPARepository;
        this.inventarioQueryRepository = inventarioQueryRepository;
        this.productoJPARepository = productoJPARepository;
        this.inventarioMapper = inventarioMapper;
    }

    @Override
    @Transactional
    public InventarioDto create(CreateInventarioDto createDto) {
        // Verificar que el producto existe
        productoJPARepository.findById(createDto.getProducto_id())
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        // Verificar que no exista inventario para este producto
        Boolean exists = inventarioQueryRepository.existsByProductoId(createDto.getProducto_id());
        if (exists) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe inventario para este producto");
        }
        // Validar que stock sea >= 0
        if (createDto.getStock() < 0) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "El stock no puede ser negativo");
        }
        try {
            InventarioEntity entity = inventarioMapper.createToEntity(createDto);
            InventarioEntity savedEntity = inventarioJPARepository.save(entity);
            return inventarioQueryRepository.findByProductoId(savedEntity.getProducto_id());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear el inventario: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Boolean update(UpdateInventarioDto updateDto) {
        InventarioEntity entity = inventarioJPARepository.findById(updateDto.getId())
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Inventario no encontrado"));

        // Validar que stock sea >= 0
        if (updateDto.getStock() != null && updateDto.getStock() < 0) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "El stock no puede ser negativo");
        }

        try {
            inventarioMapper.updateEntityFromDto(updateDto, entity);
            inventarioJPARepository.save(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar el inventario");
        }
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        InventarioEntity entity = inventarioJPARepository.findById(id)
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Inventario no encontrado"));

        try {
            entity.setDeleted_at(LocalDateTime.now());
            entity.setActivo(2L);
            inventarioJPARepository.save(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar el inventario");
        }
    }

    @Override
    public InventarioDto findById(Long id) {
        InventarioEntity entity = inventarioJPARepository.findById(id)
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Inventario no encontrado"));
        InventarioDto dto = inventarioMapper.toDto(entity);
        return dto;
    }

    @Override
    public InventarioDto findByProductoId(Long productoId) {
        InventarioDto dto = inventarioQueryRepository.findByProductoId(productoId);
        if (dto == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "No hay inventario para este producto");
        }
        return dto;
    }

    @Override
    public List<InventarioDto> findAllActive() {
        return inventarioQueryRepository.findAllActive();
    }

    @Override
    public Page<InventarioTableDto> pageInventario(PageableDto<Object> pageableDto) {
        return inventarioQueryRepository.listInventario(pageableDto);
    }

    @Override
    @Transactional
    public Boolean ajustarStock(AjusteStockDto ajusteDto) {
        if (ajusteDto.getTipo().equals("SUMA")) {
            return sumarStock(ajusteDto.getProducto_id(), ajusteDto.getCantidad());
        } else if (ajusteDto.getTipo().equals("RESTA")) {
            return restarStock(ajusteDto.getProducto_id(), ajusteDto.getCantidad());
        } else {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Tipo de ajuste inválido. Use SUMA o RESTA");
        }
    }

    @Override
    @Transactional
    public Boolean sumarStock(Long productoId, Integer cantidad) {
        InventarioDto inventarioDto = inventarioQueryRepository.findByProductoId(productoId);
        if (inventarioDto == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "No existe inventario para este producto");
        }

        InventarioEntity entity = inventarioJPARepository.findById(inventarioDto.getId())
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Inventario no encontrado"));

        try {
            entity.setStock(entity.getStock() + cantidad);
            inventarioJPARepository.save(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al sumar stock");
        }
    }

    @Override
    @Transactional
    public Boolean restarStock(Long productoId, Integer cantidad) {
        InventarioDto inventarioDto = inventarioQueryRepository.findByProductoId(productoId);
        if (inventarioDto == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "No existe inventario para este producto");
        }

        InventarioEntity entity = inventarioJPARepository.findById(inventarioDto.getId())
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Inventario no encontrado"));

        Integer nuevoStock = entity.getStock() - cantidad;
        if (nuevoStock < 0) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Stock insuficiente. Stock actual: " + entity.getStock());
        }

        try {
            entity.setStock(nuevoStock);
            inventarioJPARepository.save(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al restar stock");
        }
    }
}