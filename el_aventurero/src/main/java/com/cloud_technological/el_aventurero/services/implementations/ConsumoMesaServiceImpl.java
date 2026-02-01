package com.cloud_technological.el_aventurero.services.implementations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud_technological.el_aventurero.dto.consumo_mesa.CreateConsumoMesaDto;
import com.cloud_technological.el_aventurero.dto.consumo_mesa.ConsumoMesaDto;
import com.cloud_technological.el_aventurero.dto.consumo_mesa.ConsumoMesaTableDto;
import com.cloud_technological.el_aventurero.dto.consumo_mesa.TotalMesaDto;
import com.cloud_technological.el_aventurero.dto.consumo_mesa.UpdateConsumoMesaDto;
import com.cloud_technological.el_aventurero.dto.inventario.InventarioDto;
import com.cloud_technological.el_aventurero.entity.ConsumoMesaEntity;
import com.cloud_technological.el_aventurero.entity.MesaEntity;
import com.cloud_technological.el_aventurero.mappers.consumo_mesa.ConsumoMesaMapper;
import com.cloud_technological.el_aventurero.repositories.consumo_mesa.ConsumoMesaJPARepository;
import com.cloud_technological.el_aventurero.repositories.consumo_mesa.ConsumoMesaQueryRepository;
import com.cloud_technological.el_aventurero.repositories.mesas.MesaJPARepository;
import com.cloud_technological.el_aventurero.repositories.productos.ProductoJPARepository;
import com.cloud_technological.el_aventurero.services.ConsumoMesaService;
import com.cloud_technological.el_aventurero.services.InventarioService;
import com.cloud_technological.el_aventurero.util.GlobalException;
import com.cloud_technological.el_aventurero.util.PageableDto;

@Service
public class ConsumoMesaServiceImpl implements ConsumoMesaService {

    private final ConsumoMesaJPARepository consumoMesaJPARepository;
    private final ConsumoMesaQueryRepository consumoMesaQueryRepository;
    private final MesaJPARepository mesaJPARepository;
    private final ProductoJPARepository productoJPARepository;
    private final InventarioService inventarioService;
    private final ConsumoMesaMapper consumoMesaMapper;

    public ConsumoMesaServiceImpl(
        ConsumoMesaJPARepository consumoMesaJPARepository,
        ConsumoMesaQueryRepository consumoMesaQueryRepository,
        MesaJPARepository mesaJPARepository,
        ProductoJPARepository productoJPARepository,
        InventarioService inventarioService,
        ConsumoMesaMapper consumoMesaMapper
    ) {
        this.consumoMesaJPARepository = consumoMesaJPARepository;
        this.consumoMesaQueryRepository = consumoMesaQueryRepository;
        this.mesaJPARepository = mesaJPARepository;
        this.productoJPARepository = productoJPARepository;
        this.inventarioService = inventarioService;
        this.consumoMesaMapper = consumoMesaMapper;
    }

    @Override
    @Transactional
    public ConsumoMesaDto create(CreateConsumoMesaDto createDto) {
        System.out.println("========== CREATE CONSUMO MESA - INICIO ==========");
        
        // Validar que la mesa existe
        MesaEntity mesa = mesaJPARepository.findById(createDto.getMesa_id())
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));

        // Validar que el producto existe
        productoJPARepository.findById(createDto.getProducto_id())
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        // Obtener precio del inventario
        InventarioDto inventario = inventarioService.findById(createDto.getProducto_id());
        if (inventario == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "No hay inventario para este producto");
        }

        // Validar stock disponible
        if (inventario.getStock() < createDto.getCantidad()) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, 
                "Stock insuficiente. Disponible: " + inventario.getStock());
        }

        // Validar cantidad positiva
        if (createDto.getCantidad() <= 0) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");
        }

        try {
            ConsumoMesaEntity entity = new ConsumoMesaEntity();
            entity.setMesa_id(createDto.getMesa_id());
            entity.setProducto_id(createDto.getProducto_id());
            entity.setCantidad(createDto.getCantidad());
            entity.setPrecio_unitario(inventario.getPrecio_venta());
            
            // Calcular subtotal
            BigDecimal subtotal = inventario.getPrecio_venta()
                .multiply(new BigDecimal(createDto.getCantidad()));
            entity.setSubtotal(subtotal);
            
            ConsumoMesaEntity savedEntity = consumoMesaJPARepository.save(entity);

            // *** RESTAR STOCK DEL INVENTARIO ***
            inventarioService.restarStock(createDto.getProducto_id(), createDto.getCantidad());
            System.out.println("Stock restado: " + createDto.getCantidad() + " unidades");

            // Actualizar estado de la mesa a OCUPADA si está LIBRE
            if (mesa.getEstado().equals("LIBRE")) {
                mesa.setEstado("OCUPADA");
            }

            // Calcular y actualizar total acumulado de la mesa
            TotalMesaDto totalMesa = consumoMesaQueryRepository.getTotalByMesaId(createDto.getMesa_id());
            
            if (totalMesa != null && totalMesa.getTotal() != null) {
                mesa.setTotal_acumulado(totalMesa.getTotal());
            } else {
                mesa.setTotal_acumulado(BigDecimal.ZERO);
            }
            
            mesaJPARepository.save(mesa);

            return consumoMesaQueryRepository.findByMesaId(createDto.getMesa_id())
                .stream()
                .filter(c -> c.getId().equals(savedEntity.getId()))
                .findFirst()
                .orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear el consumo: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Boolean update(UpdateConsumoMesaDto updateDto) {
        ConsumoMesaEntity entity = consumoMesaJPARepository.findById(updateDto.getId())
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Consumo no encontrado"));

        // Validar cantidad positiva
        if (updateDto.getCantidad() <= 0) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "La cantidad debe ser mayor a 0");
        }

        // Calcular diferencia de stock
        Integer cantidadAnterior = entity.getCantidad();
        Integer diferencia = updateDto.getCantidad() - cantidadAnterior;

        // Validar stock disponible si aumenta cantidad
        if (diferencia > 0) {
            InventarioDto inventario = inventarioService.findByProductoId(entity.getProducto_id());
            if (inventario.getStock() < diferencia) {
                throw new GlobalException(HttpStatus.BAD_REQUEST, 
                    "Stock insuficiente. Disponible: " + inventario.getStock());
            }
        }

        try {
            entity.setCantidad(updateDto.getCantidad());
            
            // Recalcular subtotal
            BigDecimal subtotal = entity.getPrecio_unitario()
                .multiply(new BigDecimal(updateDto.getCantidad()));
            entity.setSubtotal(subtotal);
            
            consumoMesaJPARepository.save(entity);

            // *** AJUSTAR STOCK SEGÚN LA DIFERENCIA ***
            if (diferencia > 0) {
                // Aumentó cantidad → restar más stock
                inventarioService.restarStock(entity.getProducto_id(), diferencia);
                System.out.println("Stock restado adicional: " + diferencia);
            } else if (diferencia < 0) {
                // Disminuyó cantidad → devolver stock
                inventarioService.sumarStock(entity.getProducto_id(), Math.abs(diferencia));
                System.out.println("Stock devuelto: " + Math.abs(diferencia));
            }

            // Actualizar total acumulado de la mesa
            MesaEntity mesa = mesaJPARepository.findById(entity.getMesa_id())
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
            
            TotalMesaDto totalMesa = consumoMesaQueryRepository.getTotalByMesaId(entity.getMesa_id());
            if (totalMesa != null && totalMesa.getTotal() != null) {
                mesa.setTotal_acumulado(totalMesa.getTotal());
            } else {
                mesa.setTotal_acumulado(BigDecimal.ZERO);
            }
            mesaJPARepository.save(mesa);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar el consumo");
        }
    }

    @Override
    @Transactional
    public Boolean delete(Long id) {
        ConsumoMesaEntity entity = consumoMesaJPARepository.findById(id)
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Consumo no encontrado"));

        try {
            entity.setDeleted_at(LocalDateTime.now());
            entity.setActivo(2L);
            consumoMesaJPARepository.save(entity);

            // *** DEVOLVER STOCK AL INVENTARIO ***
            inventarioService.sumarStock(entity.getProducto_id(), entity.getCantidad());
            System.out.println("Stock devuelto: " + entity.getCantidad() + " unidades");

            // Actualizar total acumulado de la mesa
            MesaEntity mesa = mesaJPARepository.findById(entity.getMesa_id())
                .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));
            
            TotalMesaDto totalMesa = consumoMesaQueryRepository.getTotalByMesaId(entity.getMesa_id());
            if (totalMesa != null && totalMesa.getTotal() != null) {
                mesa.setTotal_acumulado(totalMesa.getTotal());
            } else {
                mesa.setTotal_acumulado(BigDecimal.ZERO);
            }

            // Si no quedan consumos, cambiar mesa a LIBRE
            List<ConsumoMesaDto> consumos = consumoMesaQueryRepository.findByMesaId(entity.getMesa_id());
            if (consumos.isEmpty()) {
                mesa.setEstado("LIBRE");
                mesa.setTotal_acumulado(BigDecimal.ZERO);
            }

            mesaJPARepository.save(mesa);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar el consumo");
        }
    }

    @Override
    public ConsumoMesaDto findById(Long id) {
        ConsumoMesaEntity entity = consumoMesaJPARepository.findById(id)
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Consumo no encontrado"));
        ConsumoMesaDto dto = consumoMesaMapper.toDto(entity);
        return dto;
    }

    @Override
    public List<ConsumoMesaDto> findByMesaId(Long mesaId) {
        // Validar que la mesa existe
        mesaJPARepository.findById(mesaId)
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));

        return consumoMesaQueryRepository.findByMesaId(mesaId);
    }

    @Override
    public TotalMesaDto getTotalByMesaId(Long mesaId) {
        // Validar que la mesa existe
        mesaJPARepository.findById(mesaId)
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));

        return consumoMesaQueryRepository.getTotalByMesaId(mesaId);
    }

    @Override
    public Page<ConsumoMesaTableDto> pageConsumoMesa(PageableDto<Object> pageableDto) {
        return consumoMesaQueryRepository.listConsumoMesa(pageableDto);
    }

    @Override
    @Transactional
    public Boolean deleteByMesaId(Long mesaId) {
        // Validar que la mesa existe
        mesaJPARepository.findById(mesaId)
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));

        return consumoMesaQueryRepository.deleteByMesaId(mesaId);
    }
}