package com.cloud_technological.el_aventurero.repositories.productos;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cloud_technological.el_aventurero.dto.productos.ProductoDto;
import com.cloud_technological.el_aventurero.dto.productos.ProductoTableDto;
import com.cloud_technological.el_aventurero.util.MapperRepository;
import com.cloud_technological.el_aventurero.util.PageableDto;

@Repository
public class ProductoQueryRepository {
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Boolean existsByNombre(String nombre) {
        String sql = "SELECT COUNT(*) FROM productos WHERE LOWER(nombre) = LOWER(:nombre) AND deleted_at IS NULL";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nombre", nombre);
        
        Long count = namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
        return count != null && count > 0;
    }

    // ✅ ACTUALIZADO - Incluye nuevos campos
    public List<ProductoDto> findAllActive() {
        String sql = """
            SELECT
                id,
                nombre,
                tipo_venta,
                tipo,
                categoria,
                unidad_medida,
                activo,
                created_at,
                updated_at
            FROM productos
            WHERE activo = 1 AND deleted_at IS NULL
            ORDER BY tipo, categoria, nombre ASC
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        return MapperRepository.mapListToDtoList(resultList, ProductoDto.class);
    }

    // ✅ ACTUALIZADO - Incluye nuevos campos y permite filtrar por tipo
    public PageImpl<ProductoTableDto> listProductos(PageableDto<Object> pageableDto) {
        int pageNumber = pageableDto.getPage() != null ? pageableDto.getPage().intValue() : 0;
        int pageSize = pageableDto.getRows() != null ? pageableDto.getRows().intValue() : 10;
        String search = pageableDto.getSearch() != null ? pageableDto.getSearch().trim() : null;

        StringBuilder sql = new StringBuilder("""
            SELECT
                p.id,
                p.nombre,
                p.tipo_venta,
                p.tipo,
                p.categoria,
                p.unidad_medida,
                p.activo,
                COUNT(*) OVER() AS total_rows
            FROM productos p
            WHERE p.deleted_at IS NULL
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (search != null && !search.isEmpty()) {
            sql.append(" AND LOWER(p.nombre) ILIKE :search");
            params.addValue("search", "%" + search.toLowerCase() + "%");
        }

        if (pageableDto.getOrder_by() != null && !pageableDto.getOrder_by().isEmpty()) {
            sql.append(" ORDER BY ").append(pageableDto.getOrder_by()).append(" ").append(pageableDto.getOrder());
        } else {
            sql.append(" ORDER BY p.tipo, p.categoria, p.nombre ASC");
        }

        sql.append(" OFFSET :offset LIMIT :limit");
        long offset = pageNumber * pageSize;
        params.addValue("offset", offset);
        params.addValue("limit", pageSize);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql.toString(), params, new ColumnMapRowMapper()
        );
        
        List<ProductoTableDto> result = MapperRepository.mapListToDtoList(resultList, ProductoTableDto.class);
        long count = resultList.isEmpty() ? 0 : ((Number) resultList.get(0).get("total_rows")).longValue();
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);

        return new PageImpl<>(result, pageable, count);
    }

    // ✅ NUEVO - Obtener solo productos vendibles (para menú de ventas)
    public List<ProductoDto> findProductosVendibles() {
        String sql = """
            SELECT
                id,
                nombre,
                tipo_venta,
                tipo,
                categoria,
                unidad_medida,
                activo,
                created_at,
                updated_at
            FROM productos
            WHERE deleted_at IS NULL
            AND activo = 1
            AND tipo = 'PRODUCTO'
            ORDER BY categoria, nombre ASC
        """;
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, new ColumnMapRowMapper()
        );
        
        return MapperRepository.mapListToDtoList(resultList, ProductoDto.class);
    }

    // ✅ NUEVO - Obtener solo insumos
    public List<ProductoDto> findInsumos() {
        String sql = """
            SELECT
                id,
                nombre,
                tipo_venta,
                tipo,
                categoria,
                unidad_medida,
                activo,
                created_at,
                updated_at
            FROM productos
            WHERE deleted_at IS NULL
            AND tipo = 'INSUMO'
            ORDER BY categoria, nombre ASC
        """;
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, new ColumnMapRowMapper()
        );
        
        return MapperRepository.mapListToDtoList(resultList, ProductoDto.class);
    }

    // ✅ NUEVO - Buscar por tipo (PRODUCTO o INSUMO)
    public List<ProductoDto> findByTipo(String tipo) {
        String sql = """
            SELECT
                id,
                nombre,
                tipo_venta,
                tipo,
                categoria,
                unidad_medida,
                activo,
                created_at,
                updated_at
            FROM productos
            WHERE deleted_at IS NULL
            AND tipo = :tipo
            ORDER BY categoria, nombre ASC
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("tipo", tipo);
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        return MapperRepository.mapListToDtoList(resultList, ProductoDto.class);
    }

    // ✅ NUEVO - Buscar por ID (útil para otros servicios)
    public ProductoDto findById(Long id) {
        String sql = """
            SELECT
                id,
                nombre,
                tipo_venta,
                tipo,
                categoria,
                unidad_medida,
                activo,
                created_at,
                updated_at
            FROM productos
            WHERE id = :id
            AND deleted_at IS NULL
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        if (resultList.isEmpty()) {
            return null;
        }
        
        List<ProductoDto> result = MapperRepository.mapListToDtoList(resultList, ProductoDto.class);
        return result.get(0);
    }
    // ✅ NUEVO - Listar con filtro de tipo específico
    public PageImpl<ProductoTableDto> listProductosPorTipo(PageableDto<Object> pageableDto, String tipo) {
        int pageNumber = pageableDto.getPage() != null ? pageableDto.getPage().intValue() : 0;
        int pageSize = pageableDto.getRows() != null ? pageableDto.getRows().intValue() : 10;
        String search = pageableDto.getSearch() != null ? pageableDto.getSearch().trim() : null;

        StringBuilder sql = new StringBuilder("""
            SELECT
                p.id,
                p.nombre,
                p.tipo_venta,
                p.tipo,
                p.categoria,
                p.unidad_medida,
                p.activo,
                COUNT(*) OVER() AS total_rows
            FROM productos p
            WHERE p.deleted_at IS NULL
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        // ✅ Filtrar por tipo si no es "TODOS"
        if (tipo != null && !tipo.equals("TODOS")) {
            sql.append(" AND p.tipo = :tipo");
            params.addValue("tipo", tipo);
        }

        if (search != null && !search.isEmpty()) {
            sql.append(" AND LOWER(p.nombre) ILIKE :search");
            params.addValue("search", "%" + search.toLowerCase() + "%");
        }

        if (pageableDto.getOrder_by() != null && !pageableDto.getOrder_by().isEmpty()) {
            sql.append(" ORDER BY ").append(pageableDto.getOrder_by()).append(" ").append(pageableDto.getOrder());
        } else {
            sql.append(" ORDER BY p.tipo, p.categoria, p.nombre ASC");
        }

        sql.append(" OFFSET :offset LIMIT :limit");
        long offset = pageNumber * pageSize;
        params.addValue("offset", offset);
        params.addValue("limit", pageSize);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql.toString(), params, new ColumnMapRowMapper()
        );
        
        List<ProductoTableDto> result = MapperRepository.mapListToDtoList(resultList, ProductoTableDto.class);
        long count = resultList.isEmpty() ? 0 : ((Number) resultList.get(0).get("total_rows")).longValue();
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);

        return new PageImpl<>(result, pageable, count);
    }
}