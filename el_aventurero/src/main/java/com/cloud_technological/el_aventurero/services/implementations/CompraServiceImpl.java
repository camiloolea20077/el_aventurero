package com.cloud_technological.el_aventurero.services.implementations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud_technological.el_aventurero.dto.compras.CompraDto;
import com.cloud_technological.el_aventurero.dto.compras.CompraTableDto;
import com.cloud_technological.el_aventurero.dto.compras.CreateCompraDto;
import com.cloud_technological.el_aventurero.dto.detalle_compras.CreateDetalleCompraDto;
import com.cloud_technological.el_aventurero.dto.detalle_compras.DetalleCompraDto;
import com.cloud_technological.el_aventurero.entity.CompraEntity;
import com.cloud_technological.el_aventurero.entity.DetalleCompraEntity;
import com.cloud_technological.el_aventurero.entity.MovimientoCajaEntity;
import com.cloud_technological.el_aventurero.mappers.compras.CompraMapper;
import com.cloud_technological.el_aventurero.repositories.compras.CompraJPARepository;
import com.cloud_technological.el_aventurero.repositories.compras.CompraQueryRepository;
import com.cloud_technological.el_aventurero.repositories.detalle_compras.DetalleCompraJPARepository;
import com.cloud_technological.el_aventurero.repositories.detalle_compras.DetalleCompraQueryRepository;
import com.cloud_technological.el_aventurero.repositories.movimiento_caja.MovimientoCajaJPARepository;
import com.cloud_technological.el_aventurero.repositories.productos.ProductoJPARepository;
import com.cloud_technological.el_aventurero.services.CompraService;
import com.cloud_technological.el_aventurero.services.InventarioService;
import com.cloud_technological.el_aventurero.util.GlobalException;
import com.cloud_technological.el_aventurero.util.PageableDto;

@Service
public class CompraServiceImpl implements CompraService {

    private final CompraJPARepository compraJPARepository;
    private final CompraQueryRepository compraQueryRepository;
    private final DetalleCompraJPARepository detalleCompraJPARepository;
    private final DetalleCompraQueryRepository detalleCompraQueryRepository;
    private final ProductoJPARepository productoJPARepository;
    private final InventarioService inventarioService;
    private final CompraMapper compraMapper;
    private final MovimientoCajaJPARepository movimientoCajaJPARepository;

    public CompraServiceImpl(
        CompraJPARepository compraJPARepository,
        CompraQueryRepository compraQueryRepository,
        DetalleCompraJPARepository detalleCompraJPARepository,
        DetalleCompraQueryRepository detalleCompraQueryRepository,
        ProductoJPARepository productoJPARepository,
        InventarioService inventarioService,
        MovimientoCajaJPARepository movimientoCajaJPARepository,
        CompraMapper compraMapper
    ) {
        this.compraJPARepository = compraJPARepository;
        this.compraQueryRepository = compraQueryRepository;
        this.detalleCompraJPARepository = detalleCompraJPARepository;
        this.detalleCompraQueryRepository = detalleCompraQueryRepository;
        this.productoJPARepository = productoJPARepository;
        this.movimientoCajaJPARepository = movimientoCajaJPARepository;
        this.inventarioService = inventarioService;
        this.compraMapper = compraMapper;
    }

    @Override
    @Transactional
    public CompraDto create(CreateCompraDto createDto) {
        // Validar que haya detalles
        if (createDto.getDetalles() == null || createDto.getDetalles().isEmpty()) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Debe agregar al menos un producto");
        }

        try {
            // Calcular total de la compra
            BigDecimal totalCompra = BigDecimal.ZERO;
            
            for (CreateDetalleCompraDto detalle : createDto.getDetalles()) {
                // Validar que el producto existe
                productoJPARepository.findById(detalle.getProducto_id())
                    .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, 
                        "Producto no encontrado: " + detalle.getProducto_id()));
                
                totalCompra = totalCompra.add(detalle.getCosto_total());
            }

            // Crear compra
            CompraEntity compra = new CompraEntity();
            compra.setTotal_compra(totalCompra);
            compra.setMetodo_pago(createDto.getMetodo_pago());
            CompraEntity savedCompra = compraJPARepository.save(compra);

            // Crear detalles y actualizar inventario
            for (CreateDetalleCompraDto detalleDto : createDto.getDetalles()) {
                // Calcular total_unidades
                Integer totalUnidades = (detalleDto.getCajas() * detalleDto.getUnidades_por_caja());
                
                // Calcular costo_unitario
                BigDecimal costoUnitario = detalleDto.getCosto_total()
                    .divide(new BigDecimal(totalUnidades), 2, BigDecimal.ROUND_HALF_UP);

                // Crear detalle
                DetalleCompraEntity detalle = new DetalleCompraEntity();
                detalle.setCompra_id(savedCompra.getId());
                detalle.setProducto_id(detalleDto.getProducto_id());
                detalle.setCajas(detalleDto.getCajas());
                detalle.setUnidades_por_caja(detalleDto.getUnidades_por_caja());
                detalle.setTotal_unidades(totalUnidades);
                detalle.setCosto_total(detalleDto.getCosto_total());
                detalle.setCosto_unitario(costoUnitario);
                detalle.setPrecio_sugerido(detalleDto.getPrecio_sugerido());
                detalle.setPrecio_venta(detalleDto.getPrecio_venta());
                
                detalleCompraJPARepository.save(detalle);

                // Sumar stock al inventario
                inventarioService.sumarStock(detalleDto.getProducto_id(), totalUnidades);
            }

            // ====== NUEVO: Registrar automáticamente en Flujo de Caja ======
            try {
                MovimientoCajaEntity movimiento = new MovimientoCajaEntity();
                movimiento.setTipo("EGRESO");
                movimiento.setConcepto("Compra de productos");
                movimiento.setCategoria("COMPRA");
                movimiento.setMonto(totalCompra); // ✅ Usar el total calculado
                movimiento.setMetodo_pago(createDto.getMetodo_pago());
                movimiento.setFecha(LocalDate.now());
                movimiento.setCompra_id(savedCompra.getId());
                movimiento.setDescripcion("Compra automática con " + createDto.getDetalles().size() + " productos");
                
                movimientoCajaJPARepository.save(movimiento);
            } catch (Exception e) {
                // Log error pero no fallar la compra
                System.err.println("Error registrando movimiento de caja para compra: " + e.getMessage());
            }
            // ================================================================

            // Retornar compra con detalles
            CompraDto compraDto = compraQueryRepository.findByIdWithDetails(savedCompra.getId());
            List<DetalleCompraDto> detalles = detalleCompraQueryRepository.findByCompraId(savedCompra.getId());
            compraDto.setDetalles(detalles);
            
            return compraDto;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al crear la compra: " + e.getMessage(), e);
        }
    }
    @Override
    @Transactional
    public Boolean delete(Long id) {
        CompraEntity entity = compraJPARepository.findById(id)
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Compra no encontrada"));

        try {
            entity.setDeleted_at(LocalDateTime.now());
            entity.setActivo(2L);
            compraJPARepository.save(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar la compra");
        }
    }

    @Override
    public CompraDto findById(Long id) {
        CompraDto compraDto = compraQueryRepository.findByIdWithDetails(id);
        if (compraDto == null) {
            throw new GlobalException(HttpStatus.NOT_FOUND, "Compra no encontrada");
        }
        
        List<DetalleCompraDto> detalles = detalleCompraQueryRepository.findByCompraId(id);
        compraDto.setDetalles(detalles);
        
        return compraDto;
    }

    @Override
    public Page<CompraTableDto> pageCompras(PageableDto<Object> pageableDto) {
        return compraQueryRepository.listCompras(pageableDto);
    }
}