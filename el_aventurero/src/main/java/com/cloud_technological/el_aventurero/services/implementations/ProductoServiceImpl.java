package com.cloud_technological.el_aventurero.services.implementations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cloud_technological.el_aventurero.dto.productos.CreateProductoDto;
import com.cloud_technological.el_aventurero.dto.productos.ProductoDto;
import com.cloud_technological.el_aventurero.dto.productos.ProductoTableDto;
import com.cloud_technological.el_aventurero.dto.productos.UpdateProductoDto;
import com.cloud_technological.el_aventurero.entity.ProductoEntity;
import com.cloud_technological.el_aventurero.mappers.productos.ProductoMapper;
import com.cloud_technological.el_aventurero.repositories.productos.ProductoJPARepository;
import com.cloud_technological.el_aventurero.repositories.productos.ProductoQueryRepository;
import com.cloud_technological.el_aventurero.services.ProductoService;
import com.cloud_technological.el_aventurero.util.GlobalException;
import com.cloud_technological.el_aventurero.util.PageableDto;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoJPARepository productoJPARepository;
    private final ProductoQueryRepository productoQueryRepository;
    private final ProductoMapper productoMapper;

    public ProductoServiceImpl(
        ProductoJPARepository productoJPARepository,
        ProductoQueryRepository productoQueryRepository,
        ProductoMapper productoMapper
    ) {
        this.productoJPARepository = productoJPARepository;
        this.productoQueryRepository = productoQueryRepository;
        this.productoMapper = productoMapper;
    }

    @Override
    @Transactional
    public ProductoDto create(CreateProductoDto createDto) {

        if (!createDto.getTipo_venta().equals("UNIDAD") && !createDto.getTipo_venta().equals("BOTELLA")) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Tipo de venta debe ser UNIDAD o BOTELLA");
        }

        Boolean exists = productoQueryRepository.existsByNombre(createDto.getNombre());
        if (exists) {
            throw new GlobalException(HttpStatus.BAD_REQUEST, "Ya existe un producto con ese nombre");
        }

        try {
            ProductoEntity entity = productoMapper.createToEntity(createDto);
            ProductoEntity savedEntity = productoJPARepository.save(entity);
            return productoMapper.toDto(savedEntity);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear el producto: " + e.getMessage(), e);
        }
    }
    @Override
	public Boolean delete(Long id) {
		productoJPARepository.findById(id)
				.orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Elemento no encontrado"));
                productoJPARepository.deleteById(id);
		return true;
	}

    @Override
    public ProductoDto findById(Long id) {
        ProductoEntity entity = productoJPARepository.findById(id)
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        
        return productoMapper.toDto(entity);
    }

    @Override
    @Transactional
    public Boolean update(UpdateProductoDto updateDto) {
        ProductoEntity entity = productoJPARepository.findById(updateDto.getId())
            .orElseThrow(() -> new GlobalException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        if (updateDto.getTipo_venta() != null) {
            if (!updateDto.getTipo_venta().equals("UNIDAD") && !updateDto.getTipo_venta().equals("BOTELLA")) {
                throw new GlobalException(HttpStatus.BAD_REQUEST, "Tipo de venta debe ser UNIDAD o BOTELLA");
            }
        }

        try {
            productoMapper.updateEntityFromDto(updateDto, entity);
            productoJPARepository.save(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar el producto");
        }
    }
    @Override
    public List<ProductoDto> findAllActive() {
        return productoQueryRepository.findAllActive();
    }

    @Override
    public PageImpl<ProductoTableDto> pageProductos(PageableDto<Object> pageableDto) {
        return productoQueryRepository.listProductos(pageableDto);
    }
}