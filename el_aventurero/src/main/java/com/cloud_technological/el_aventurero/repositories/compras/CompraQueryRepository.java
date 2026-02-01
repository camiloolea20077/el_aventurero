package com.cloud_technological.el_aventurero.repositories.compras;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cloud_technological.el_aventurero.dto.compras.CompraDto;
import com.cloud_technological.el_aventurero.dto.compras.CompraTableDto;
import com.cloud_technological.el_aventurero.util.MapperRepository;
import com.cloud_technological.el_aventurero.util.PageableDto;

@Repository
public class CompraQueryRepository {
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public CompraDto findByIdWithDetails(Long id) {
        String sql = """
            SELECT
                c.id,
                c.total_compra,
                c.metodo_pago,
                c.activo,
                c.created_at,
                c.updated_at
            FROM compras c
            WHERE c.id = :id
            AND c.deleted_at IS NULL
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        
        return namedParameterJdbcTemplate.query(sql, params, (rs) -> {
            if (!rs.next()) {
                return null;
            }
            CompraDto dto = new CompraDto();
            dto.setId(rs.getLong("id"));
            dto.setTotal_compra(rs.getBigDecimal("total_compra"));
            dto.setMetodo_pago(rs.getString("metodo_pago"));
            dto.setActivo(rs.getLong("activo"));
            return dto;
        });
    }

    public PageImpl<CompraTableDto> listCompras(PageableDto<Object> pageableDto) {
        int pageNumber = pageableDto.getPage() != null ? pageableDto.getPage().intValue() : 0;
        int pageSize = pageableDto.getRows() != null ? pageableDto.getRows().intValue() : 10;
        String search = pageableDto.getSearch() != null ? pageableDto.getSearch().trim() : null;

        StringBuilder sql = new StringBuilder("""
            SELECT
                c.id,
                c.total_compra::text AS total_compra,
                c.metodo_pago,
                COUNT(dc.id) AS cantidad_productos,
                c.created_at,
                c.activo,
                COUNT(*) OVER() AS total_rows
            FROM compras c
            LEFT JOIN detalle_compra dc ON dc.compra_id = c.id AND dc.deleted_at IS NULL
            WHERE c.deleted_at IS NULL
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (search != null && !search.isEmpty()) {
            sql.append(" AND LOWER(c.metodo_pago) ILIKE :search");
            params.addValue("search", "%" + search.toLowerCase() + "%");
        }

        sql.append(" GROUP BY c.id, c.total_compra, c.metodo_pago, c.created_at, c.activo");

        // Validación de ORDER BY
        String orderBy = "c.created_at";
        if (pageableDto.getOrder_by() != null && !pageableDto.getOrder_by().isEmpty()) {
            String[] validColumns = {"id", "total_compra", "metodo_pago", "created_at"};
            if (java.util.Arrays.asList(validColumns).contains(pageableDto.getOrder_by())) {
                orderBy = "c." + pageableDto.getOrder_by();
            }
        }
        
        String order = "DESC";
        if ("ASC".equalsIgnoreCase(pageableDto.getOrder())) {
            order = "ASC";
        }
        
        sql.append(" ORDER BY ").append(orderBy).append(" ").append(order);

        sql.append(" OFFSET :offset LIMIT :limit");
        long offset = pageNumber * pageSize;
        params.addValue("offset", offset);
        params.addValue("limit", pageSize);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql.toString(), params, new ColumnMapRowMapper()
        );
        
        List<CompraTableDto> result = MapperRepository.mapListToDtoList(resultList, CompraTableDto.class);
        long count = resultList.isEmpty() ? 0 : ((Number) resultList.get(0).get("total_rows")).longValue();
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);

        return new PageImpl<>(result, pageable, count);
    }
}