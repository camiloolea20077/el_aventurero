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

    public List<ProductoDto> findAllActive() {
        String sql = """
            SELECT
                id,
                nombre,
                tipo_venta,
                activo,
                created_at,
                updated_at
            FROM productos
            WHERE activo = 1 AND deleted_at IS NULL
            ORDER BY nombre ASC
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        return MapperRepository.mapListToDtoList(resultList, ProductoDto.class);
    }

    public PageImpl<ProductoTableDto> listProductos(PageableDto<Object> pageableDto) {
        int pageNumber = pageableDto.getPage() != null ? pageableDto.getPage().intValue() : 0;
        int pageSize = pageableDto.getRows() != null ? pageableDto.getRows().intValue() : 10;
        String search = pageableDto.getSearch() != null ? pageableDto.getSearch().trim() : null;

        StringBuilder sql = new StringBuilder("""
            SELECT
                p.id,
                p.nombre,
                p.tipo_venta,
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
            sql.append(" ORDER BY p.id DESC");
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