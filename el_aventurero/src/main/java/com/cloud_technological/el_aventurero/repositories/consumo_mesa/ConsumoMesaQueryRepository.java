package com.cloud_technological.el_aventurero.repositories.consumo_mesa;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cloud_technological.el_aventurero.dto.consumo_mesa.ConsumoMesaDto;
import com.cloud_technological.el_aventurero.dto.consumo_mesa.ConsumoMesaTableDto;
import com.cloud_technological.el_aventurero.dto.consumo_mesa.TotalMesaDto;
import com.cloud_technological.el_aventurero.util.MapperRepository;
import com.cloud_technological.el_aventurero.util.PageableDto;

@Repository
public class ConsumoMesaQueryRepository {
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<ConsumoMesaDto> findByMesaId(Long mesaId) {
        String sql = """
            SELECT
                c.id,
                c.mesa_id,
                m.numero AS mesa_numero,
                c.producto_id,
                p.nombre AS producto_nombre,
                p.tipo_venta,
                c.cantidad,
                c.precio_unitario,
                c.subtotal,
                c.activo
            FROM consumo_mesa c
            INNER JOIN mesas m ON m.id = c.mesa_id
            INNER JOIN productos p ON p.id = c.producto_id
            WHERE c.mesa_id = :mesa_id
            AND c.deleted_at IS NULL
            AND c.activo = 1
            ORDER BY c.created_at DESC
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("mesa_id", mesaId);
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        return MapperRepository.mapListToDtoListNull(resultList, ConsumoMesaDto.class);
    }

    public TotalMesaDto getTotalByMesaId(Long mesaId) {
        String sql = """
            SELECT
                m.id AS mesa_id,
                m.numero AS mesa_numero,
                m.estado,
                COALESCE(SUM(c.subtotal), 0)::NUMERIC(12,2) AS total
            FROM mesas m
            LEFT JOIN consumo_mesa c 
                ON c.mesa_id = m.id 
                AND c.deleted_at IS NULL 
                AND c.activo = 1
            WHERE m.id = :mesa_id
            GROUP BY m.id, m.numero, m.estado
        """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("mesa_id", mesaId);

        List<TotalMesaDto> result = namedParameterJdbcTemplate.query(
            sql,
            params,
            (rs, rowNum) -> {
                TotalMesaDto dto = new TotalMesaDto();
                dto.setMesa_id(rs.getLong("mesa_id"));
                dto.setMesa_numero(rs.getInt("mesa_numero"));
                dto.setEstado(rs.getString("estado"));
                dto.setTotal(rs.getBigDecimal("total")); // CLAVE
                return dto;
            }
        );

        return result.isEmpty() ? null : result.get(0);
    }

    public PageImpl<ConsumoMesaTableDto> listConsumoMesa(PageableDto<Object> pageableDto) {
        int pageNumber = pageableDto.getPage() != null ? pageableDto.getPage().intValue() : 0;
        int pageSize = pageableDto.getRows() != null ? pageableDto.getRows().intValue() : 10;
        String search = pageableDto.getSearch() != null ? pageableDto.getSearch().trim() : null;

        StringBuilder sql = new StringBuilder("""
            SELECT
                c.id,
                c.mesa_id,
                m.numero AS mesa_numero,
                c.producto_id,
                p.nombre AS producto_nombre,
                p.tipo_venta,
                c.cantidad,
                c.precio_unitario::text AS precio_unitario,
                c.subtotal::text AS subtotal,
                c.activo,
                COUNT(*) OVER() AS total_rows
            FROM consumo_mesa c
            INNER JOIN mesas m ON m.id = c.mesa_id
            INNER JOIN productos p ON p.id = c.producto_id
            WHERE c.deleted_at IS NULL
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (search != null && !search.isEmpty()) {
            sql.append(" AND (LOWER(p.nombre) ILIKE :search OR CAST(m.numero AS TEXT) ILIKE :search)");
            params.addValue("search", "%" + search.toLowerCase() + "%");
        }

        // Validación de ORDER BY
        String orderBy = "c.created_at";
        if (pageableDto.getOrder_by() != null && !pageableDto.getOrder_by().isEmpty()) {
            String[] validColumns = {"id", "mesa_numero", "producto_nombre", "cantidad", "precio_unitario", "subtotal"};
            if (java.util.Arrays.asList(validColumns).contains(pageableDto.getOrder_by())) {
                orderBy = pageableDto.getOrder_by();
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
        
        List<ConsumoMesaTableDto> result = MapperRepository.mapListToDtoList(resultList, ConsumoMesaTableDto.class);
        long count = resultList.isEmpty() ? 0 : ((Number) resultList.get(0).get("total_rows")).longValue();
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);

        return new PageImpl<>(result, pageable, count);
    }

    public Boolean deleteByMesaId(Long mesaId) {
        String sql = """
            UPDATE consumo_mesa
            SET deleted_at = NOW(), activo = 2
            WHERE mesa_id = :mesa_id
            AND deleted_at IS NULL
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("mesa_id", mesaId);
        
        int rowsAffected = namedParameterJdbcTemplate.update(sql, params);
        return rowsAffected > 0;
    }
}