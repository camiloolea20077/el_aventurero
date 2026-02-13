package com.cloud_technological.el_aventurero.repositories.ventas;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cloud_technological.el_aventurero.dto.ventas.MetodoPagoStatsDto;
import com.cloud_technological.el_aventurero.dto.ventas.VentaDto;
import com.cloud_technological.el_aventurero.dto.ventas.VentaSemanalDto;
import com.cloud_technological.el_aventurero.dto.ventas.VentaTableDto;
import com.cloud_technological.el_aventurero.util.MapperRepository;
import com.cloud_technological.el_aventurero.util.PageableDto;

@Repository
public class VentaQueryRepository {
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public VentaDto findByIdWithDetails(Long id) {
        String sql = """
            SELECT
                v.id,
                v.mesa_id,
                m.numero AS mesa_numero,
                v.total::text AS total,
                v.metodo_pago,
                v.activo,
                v.created_at,
                v.updated_at
            FROM ventas v
            INNER JOIN mesas m ON m.id = v.mesa_id
            WHERE v.id = :id
            AND v.deleted_at IS NULL
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        if (resultList.isEmpty()) {
            return null;
        }
        
        List<VentaDto> result = MapperRepository.mapListToDtoListNull(resultList, VentaDto.class);
        return result.get(0);
    }

    public PageImpl<VentaTableDto> listVentas(PageableDto<Object> pageableDto) {
        int pageNumber = pageableDto.getPage() != null ? pageableDto.getPage().intValue() : 0;
        int pageSize = pageableDto.getRows() != null ? pageableDto.getRows().intValue() : 10;
        String search = pageableDto.getSearch() != null ? pageableDto.getSearch().trim() : null;

        StringBuilder sql = new StringBuilder("""
            SELECT
                v.id,
                v.mesa_id,
                m.numero AS mesa_numero,
                v.total::text AS total,
                v.metodo_pago,
                COUNT(dv.id) AS cantidad_productos,
                v.created_at,
                v.activo,
                COUNT(*) OVER() AS total_rows
            FROM ventas v
            INNER JOIN mesas m ON m.id = v.mesa_id
            LEFT JOIN detalle_venta dv ON dv.venta_id = v.id AND dv.deleted_at IS NULL
            WHERE v.deleted_at IS NULL
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(v.metodo_pago) ILIKE :search OR CAST(m.numero AS TEXT) ILIKE :search)");
            params.addValue("search", "%" + search.toLowerCase() + "%");
        }

        sql.append(" GROUP BY v.id, v.mesa_id, m.numero, v.total, v.metodo_pago, v.created_at, v.activo");

        // Validación de ORDER BY
        String orderBy = "v.created_at";
        if (pageableDto.getOrder_by() != null && !pageableDto.getOrder_by().isEmpty()) {
            String[] validColumns = {"id", "mesa_numero", "total", "metodo_pago", "created_at"};
            if (java.util.Arrays.asList(validColumns).contains(pageableDto.getOrder_by())) {
                orderBy = "v." + pageableDto.getOrder_by();
                if (pageableDto.getOrder_by().equals("mesa_numero")) {
                    orderBy = "m.numero";
                }
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
        
        List<VentaTableDto> result = MapperRepository.mapListToDtoListNull(resultList, VentaTableDto.class);
        long count = resultList.isEmpty() ? 0 : ((Number) resultList.get(0).get("total_rows")).longValue();
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);

        return new PageImpl<>(result, pageable, count);
    }

    public List<VentaSemanalDto> getWeeklySales() {
        String sql = """
            SELECT
                TO_CHAR(created_at, 'Day') AS dia,
                SUM(total) AS total,
                COUNT(*) AS cantidad_ventas
            FROM ventas
            WHERE deleted_at IS NULL
                AND created_at >= DATE_TRUNC('week', CURRENT_DATE)
            GROUP BY TO_CHAR(created_at, 'Day'), DATE_TRUNC('day', created_at)
            ORDER BY DATE_TRUNC('day', created_at)
        """;

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, new MapSqlParameterSource(), new ColumnMapRowMapper()
        );

        return MapperRepository.mapListToDtoListNull(resultList, VentaSemanalDto.class);
    }

    public List<MetodoPagoStatsDto> getPaymentMethodsStats() {
        String sql = """
            SELECT
                metodo_pago,
                SUM(total) AS total,
                COUNT(*) AS cantidad
            FROM ventas
            WHERE deleted_at IS NULL
                AND created_at >= DATE_TRUNC('month', CURRENT_DATE)
            GROUP BY metodo_pago
            ORDER BY total DESC
        """;

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, new MapSqlParameterSource(), new ColumnMapRowMapper()
        );

        return MapperRepository.mapListToDtoListNull(resultList, MetodoPagoStatsDto.class);
    }
}