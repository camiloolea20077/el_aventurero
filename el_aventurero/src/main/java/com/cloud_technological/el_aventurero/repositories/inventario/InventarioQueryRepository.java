package com.cloud_technological.el_aventurero.repositories.inventario;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cloud_technological.el_aventurero.dto.inventario.InventarioDto;
import com.cloud_technological.el_aventurero.dto.inventario.InventarioTableDto;
import com.cloud_technological.el_aventurero.util.MapperRepository;
import com.cloud_technological.el_aventurero.util.PageableDto;

@Repository
public class InventarioQueryRepository {
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Boolean existsByProductoId(Long productoId) {
        String sql = """
            SELECT COUNT(*) 
            FROM inventario 
            WHERE producto_id = :producto_id 
            AND deleted_at IS NULL 
            AND activo = 1
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("producto_id", productoId);
        
        Long count = namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
        return count != null && count > 0;
    }

    public InventarioDto findByProductoId(Long productoId) {
        String sql = """
            SELECT
                i.id,
                i.producto_id,
                p.nombre AS producto_nombre,
                p.tipo_venta,
                i.stock,
                CAST(i.costo_unitario AS NUMERIC) AS costo_unitario,
                CAST(i.precio_venta AS NUMERIC) AS precio_venta,
                i.activo,
                i.created_at,
                i.updated_at
            FROM inventario i
            INNER JOIN productos p ON p.id = i.producto_id
            WHERE i.producto_id = :producto_id
            AND i.deleted_at IS NULL
            AND i.activo = 1
            LIMIT 1
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("producto_id", productoId);
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        if (resultList.isEmpty()) {
            return null;
        }
        
        List<InventarioDto> result = MapperRepository.mapListToDtoList(resultList, InventarioDto.class);
        return result.get(0);
    }

    public List<InventarioDto> findAllActive() {
        String sql = """
            SELECT
                i.id,
                i.producto_id,
                p.nombre AS producto_nombre,
                p.tipo_venta,
                i.stock,
                i.costo_unitario,
                i.precio_venta,
                i.activo,
                i.created_at,
                i.updated_at
            FROM inventario i
            INNER JOIN productos p ON p.id = i.producto_id
            WHERE i.activo = 1 
            AND i.deleted_at IS NULL
            ORDER BY p.nombre ASC
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        return MapperRepository.mapListToDtoList(resultList, InventarioDto.class);
    }

    public PageImpl<InventarioTableDto> listInventario(PageableDto<Object> pageableDto) {
        int pageNumber = pageableDto.getPage() != null ? pageableDto.getPage().intValue() : 0;
        int pageSize = pageableDto.getRows() != null ? pageableDto.getRows().intValue() : 10;
        String search = pageableDto.getSearch() != null ? pageableDto.getSearch().trim() : null;

        StringBuilder sql = new StringBuilder("""
            SELECT
                i.id,
                i.producto_id,
                p.nombre AS producto_nombre,
                p.tipo_venta,
                i.stock,
                i.costo_unitario::text AS costo_unitario,
                i.precio_venta::text AS precio_venta,
                (i.stock * i.costo_unitario)::text AS valor_total,
                i.activo,
                COUNT(*) OVER() AS total_rows
            FROM inventario i
            INNER JOIN productos p ON p.id = i.producto_id
            WHERE i.deleted_at IS NULL
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (search != null && !search.isEmpty()) {
            sql.append(" AND LOWER(p.nombre) ILIKE :search");
            params.addValue("search", "%" + search.toLowerCase() + "%");
        }

        // Validación de ORDER BY para prevenir SQL injection
        String orderBy = "p.nombre";
        if (pageableDto.getOrder_by() != null && !pageableDto.getOrder_by().isEmpty()) {
            String[] validColumns = {"id", "producto_nombre", "stock", "costo_unitario", "precio_venta", "valor_total"};
            if (java.util.Arrays.asList(validColumns).contains(pageableDto.getOrder_by())) {
                orderBy = pageableDto.getOrder_by();
            }
        }
        
        String order = "ASC";
        if ("DESC".equalsIgnoreCase(pageableDto.getOrder())) {
            order = "DESC";
        }
        
        sql.append(" ORDER BY ").append(orderBy).append(" ").append(order);

        sql.append(" OFFSET :offset LIMIT :limit");
        long offset = pageNumber * pageSize;
        params.addValue("offset", offset);
        params.addValue("limit", pageSize);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql.toString(), params, new ColumnMapRowMapper()
        );
        
        List<InventarioTableDto> result = MapperRepository.mapListToDtoList(resultList, InventarioTableDto.class);
        long count = resultList.isEmpty() ? 0 : ((Number) resultList.get(0).get("total_rows")).longValue();
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);

        return new PageImpl<>(result, pageable, count);
    }
}