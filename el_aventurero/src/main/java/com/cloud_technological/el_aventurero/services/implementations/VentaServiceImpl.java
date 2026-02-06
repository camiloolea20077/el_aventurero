package com.cloud_technological.el_aventurero.services.implementations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud_technological.el_aventurero.dto.consumo_mesa.ConsumoMesaDto;
import com.cloud_technological.el_aventurero.dto.detalle_venta.DetalleVentaDto;
import com.cloud_technological.el_aventurero.dto.ventas.CreateVentaDto;
import com.cloud_technological.el_aventurero.dto.ventas.VentaDto;
import com.cloud_technological.el_aventurero.dto.ventas.VentaTableDto;
import com.cloud_technological.el_aventurero.entity.ConsumoMesaEntity;
import com.cloud_technological.el_aventurero.entity.DetalleVentaEntity;
import com.cloud_technological.el_aventurero.entity.MesaEntity;
import com.cloud_technological.el_aventurero.entity.MovimientoCajaEntity;
import com.cloud_technological.el_aventurero.entity.VentaEntity;
import com.cloud_technological.el_aventurero.repositories.consumo_mesa.ConsumoMesaJPARepository;
import com.cloud_technological.el_aventurero.repositories.consumo_mesa.ConsumoMesaQueryRepository;
import com.cloud_technological.el_aventurero.repositories.detalle_venta.DetalleVentaJPARepository;
import com.cloud_technological.el_aventurero.repositories.detalle_venta.DetalleVentaQueryRepository;
import com.cloud_technological.el_aventurero.repositories.mesas.MesaJPARepository;
import com.cloud_technological.el_aventurero.repositories.movimiento_caja.MovimientoCajaJPARepository;
import com.cloud_technological.el_aventurero.repositories.ventas.VentaJPARepository;
import com.cloud_technological.el_aventurero.repositories.ventas.VentaQueryRepository;
import com.cloud_technological.el_aventurero.services.ConsumoMesaService;
import com.cloud_technological.el_aventurero.services.VentaService;
import com.cloud_technological.el_aventurero.util.GlobalException;
import com.cloud_technological.el_aventurero.util.PageableDto;

@Service
public class VentaServiceImpl implements VentaService {

    private final VentaJPARepository ventaJPARepository;
    private final VentaQueryRepository ventaQueryRepository;
    private final DetalleVentaJPARepository detalleVentaJPARepository;
    private final DetalleVentaQueryRepository detalleVentaQueryRepository;
    private final MesaJPARepository mesaJPARepository;
    private final ConsumoMesaQueryRepository consumoMesaQueryRepository;
    private final ConsumoMesaService consumoMesaService;
    private final MovimientoCajaJPARepository movimientoCajaJPARepository;
    private final ConsumoMesaJPARepository consumoMesaJPARepository;

    public VentaServiceImpl(
        VentaJPARepository ventaJPARepository,
        VentaQueryRepository ventaQueryRepository,
        MovimientoCajaJPARepository movimientoCajaJPARepository,
        ConsumoMesaQueryRepository consumoMesaQueryRepository,
        DetalleVentaJPARepository detalleVentaJPARepository,
        DetalleVentaQueryRepository detalleVentaQueryRepository,
        MesaJPARepository mesaJPARepository,
        ConsumoMesaService consumoMesaService,
        ConsumoMesaJPARepository consumoMesaJPARepository
    ) {
        this.ventaJPARepository = ventaJPARepository;
        this.ventaQueryRepository = ventaQueryRepository;
        this.consumoMesaQueryRepository = consumoMesaQueryRepository;
        this.detalleVentaJPARepository = detalleVentaJPARepository;
        this.movimientoCajaJPARepository = movimientoCajaJPARepository;
        this.detalleVentaQueryRepository = detalleVentaQueryRepository;
        this.mesaJPARepository = mesaJPARepository;
        this.consumoMesaJPARepository = consumoMesaJPARepository;
        this.consumoMesaService = consumoMesaService;
    }

    @Override
    @Transactional
    public VentaDto create(CreateVentaDto createDto) {
        // Validar que la mesa existe
        MesaEntity mesa = mesaJPARepository.findById(createDto.getMesa_id())
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Mesa no encontrada"));

        try {
            // Obtener consumos de la mesa
            List<ConsumoMesaDto> consumos = consumoMesaQueryRepository.findByMesaId(createDto.getMesa_id());
            
            if (consumos.isEmpty()) {
                throw new GlobalException(HttpStatus.BAD_REQUEST, "La mesa no tiene consumos para cerrar cuenta");
            }

            // Calcular total de la venta
            BigDecimal totalVenta = BigDecimal.ZERO;
            for (ConsumoMesaDto consumo : consumos) {
                totalVenta = totalVenta.add(consumo.getSubtotal());
            }

            // Crear venta
            VentaEntity venta = new VentaEntity();
            venta.setMesa_id(createDto.getMesa_id());
            venta.setTotal(totalVenta);
            venta.setMetodo_pago(createDto.getMetodo_pago());
            VentaEntity savedVenta = ventaJPARepository.save(venta);

            // Crear detalles de venta desde consumos
            for (ConsumoMesaDto consumo : consumos) {
                DetalleVentaEntity detalle = new DetalleVentaEntity();
                detalle.setVenta_id(savedVenta.getId());
                detalle.setProducto_id(consumo.getProducto_id());
                detalle.setCantidad(consumo.getCantidad());
                detalle.setPrecio_unitario(consumo.getPrecio_unitario());
                detalle.setSubtotal(consumo.getSubtotal());
                detalleVentaJPARepository.save(detalle);
                ConsumoMesaEntity consumoEntity = consumoMesaJPARepository.findById(consumo.getId())
                    .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Consumo no encontrado"));
                consumoEntity.setDeleted_at(LocalDateTime.now());
                consumoMesaJPARepository.save(consumoEntity);
            }
            mesa.setEstado("LIBRE");
            mesa.setTotal_acumulado(BigDecimal.ZERO);
            mesaJPARepository.save(mesa);

            try {
                MovimientoCajaEntity movimiento = new MovimientoCajaEntity();
                movimiento.setTipo("INGRESO");
                movimiento.setConcepto("Venta Mesa #" + mesa.getNumero());
                movimiento.setCategoria("VENTA");
                movimiento.setMonto(totalVenta); // ✅ Usar el total calculado
                movimiento.setMetodo_pago(createDto.getMetodo_pago());
                movimiento.setFecha(LocalDate.now());
                movimiento.setVenta_id(savedVenta.getId());
                movimiento.setDescripcion("Venta automática desde mesa #" + mesa.getNumero());
                
                movimientoCajaJPARepository.save(movimiento);
            } catch (Exception e) {
                System.err.println("Error registrando movimiento de caja para venta: " + e.getMessage());
            }
            VentaDto ventaDto = ventaQueryRepository.findByIdWithDetails(savedVenta.getId());
            List<DetalleVentaDto> detalles = detalleVentaQueryRepository.findByVentaId(savedVenta.getId());
            ventaDto.setDetalles(detalles);
            
            return ventaDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear la venta: " + e.getMessage(), e);
        }
    }
    @Override
    @Transactional
    public Boolean delete(Long id) {
        VentaEntity entity = ventaJPARepository.findById(id)
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Venta no encontrada"));

        try {
            entity.setDeleted_at(LocalDateTime.now());
            entity.setActivo(2L);
            ventaJPARepository.save(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar la venta");
        }
    }

    @Override
    public VentaDto findById(Long id) {
        VentaDto ventaDto = ventaQueryRepository.findByIdWithDetails(id);
        if (ventaDto == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Venta no encontrada");
        }
        
        List<DetalleVentaDto> detalles = detalleVentaQueryRepository.findByVentaId(id);
        ventaDto.setDetalles(detalles);
        
        return ventaDto;
    }

    @Override
    public Page<VentaTableDto> pageVentas(PageableDto<Object> pageableDto) {
        return ventaQueryRepository.listVentas(pageableDto);
    }
}