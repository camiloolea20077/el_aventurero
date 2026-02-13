package com.cloud_technological.el_aventurero.services.implementations;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.cloud_technological.el_aventurero.entity.ProductoEntity;
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
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Debe agregar al menos un producto o insumo");
        }

        try {
            // Calcular total de la compra
            BigDecimal totalCompra = BigDecimal.ZERO;
            
            // Validar detalles antes de guardar
            for (CreateDetalleCompraDto detalle : createDto.getDetalles()) {
                // Validar que el producto existe y obtener su tipo
                ProductoEntity producto = productoJPARepository.findById(detalle.getProducto_id())
                    .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, 
                        "Producto no encontrado: " + detalle.getProducto_id()));
                
                // Asignar tipo del producto al detalle
                detalle.setTipo(producto.getTipo());
                
                // ✅ VALIDAR SEGÚN TIPO
                if ("PRODUCTO".equals(producto.getTipo())) {
                    // Si es producto, debe tener precio de venta
                    if (detalle.getPrecio_venta() == null || detalle.getPrecio_venta().compareTo(BigDecimal.ZERO) <= 0) {
                        throw new GlobalException(HttpStatus.BAD_REQUEST, 
                            "El producto '" + producto.getNombre() + "' debe tener precio de venta");
                    }
                } else if ("INSUMO".equals(producto.getTipo())) {
                    // Si es insumo, NO debe tener precios de venta
                    if (detalle.getPrecio_venta() != null || detalle.getPrecio_sugerido() != null) {
                        throw new GlobalException(HttpStatus.BAD_REQUEST, 
                            "El insumo '" + producto.getNombre() + "' no debe tener precio de venta");
                    }
                }
                
                // ✅ CALCULAR CANTIDAD según si viene por cajas o directo
                Integer cantidad;
                if (detalle.getCajas() != null && detalle.getUnidades_por_caja() != null) {
                    // Compra por cajas
                    cantidad = detalle.getCajas() * detalle.getUnidades_por_caja();
                    detalle.setCantidad(cantidad);
                } else {
                    // Compra directa
                    cantidad = detalle.getCantidad();
                }
                
                // Validar cantidad
                if (cantidad == null || cantidad <= 0) {
                    throw new GlobalException(HttpStatus.BAD_REQUEST, 
                        "La cantidad debe ser mayor a 0 para " + producto.getNombre());
                }
                
                // ✅ CALCULAR COSTO UNITARIO si no viene
                if (detalle.getCosto_unitario() == null) {
                    BigDecimal costoUnitario = detalle.getCosto_total()
                        .divide(new BigDecimal(cantidad), 2, RoundingMode.HALF_UP);
                    detalle.setCosto_unitario(costoUnitario);
                }
                
                totalCompra = totalCompra.add(detalle.getCosto_total());
            }

            // Crear compra
            CompraEntity compra = new CompraEntity();
            compra.setTotal_compra(totalCompra);
            compra.setMetodo_pago(createDto.getMetodo_pago());
            CompraEntity savedCompra = compraJPARepository.save(compra);

            // Crear detalles y actualizar inventario
            for (CreateDetalleCompraDto detalleDto : createDto.getDetalles()) {
                // Crear detalle
                DetalleCompraEntity detalle = new DetalleCompraEntity();
                detalle.setCompra_id(savedCompra.getId());
                detalle.setProducto_id(detalleDto.getProducto_id());
                detalle.setCajas(detalleDto.getCajas());
                detalle.setUnidades_por_caja(detalleDto.getUnidades_por_caja());
                detalle.setCantidad(detalleDto.getCantidad());
                detalle.setCosto_total(detalleDto.getCosto_total());
                detalle.setCosto_unitario(detalleDto.getCosto_unitario());
                detalle.setPrecio_sugerido(detalleDto.getPrecio_sugerido());
                detalle.setPrecio_venta(detalleDto.getPrecio_venta());
                detalle.setTipo(detalleDto.getTipo());
                
                detalleCompraJPARepository.save(detalle);

                // Sumar stock al inventario SOLO si es un PRODUCTO vendible
                // Los INSUMOS no manejan inventario en esta tabla
                if ("PRODUCTO".equals(detalleDto.getTipo())) {
                    inventarioService.sumarStock(detalleDto.getProducto_id(), detalleDto.getCantidad());
                }
            }

            // Registrar movimiento de caja
            try {
                MovimientoCajaEntity movimiento = new MovimientoCajaEntity();
                movimiento.setTipo("EGRESO");
                movimiento.setConcepto("Compra de productos/insumos");
                movimiento.setCategoria("COMPRA");
                movimiento.setMonto(totalCompra);
                movimiento.setMetodo_pago(createDto.getMetodo_pago());
                movimiento.setFecha(LocalDate.now());
                movimiento.setCompra_id(savedCompra.getId());
                movimiento.setDescripcion("Compra automática con " + createDto.getDetalles().size() + " items");
                
                movimientoCajaJPARepository.save(movimiento);
            } catch (Exception e) {
                System.err.println("Error registrando movimiento de caja: " + e.getMessage());
            }

            // Retornar compra con detalles
            CompraDto compraDto = compraQueryRepository.findByIdWithDetails(savedCompra.getId());
            List<DetalleCompraDto> detalles = detalleCompraQueryRepository.findByCompraId(savedCompra.getId());
            compraDto.setDetalles(detalles);
            
            return compraDto;
        } catch (GlobalException e) {
            throw e;
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