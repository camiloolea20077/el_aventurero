package com.cloud_technological.el_aventurero.repositories.mesas;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cloud_technological.el_aventurero.dto.mesas.MesaDto;
import com.cloud_technological.el_aventurero.dto.mesas.MesaTableDto;
import com.cloud_technological.el_aventurero.util.MapperRepository;
import com.cloud_technological.el_aventurero.util.PageableDto;

@Repository
public class MesaQueryRepository {
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Boolean existsByNumero(Integer numero) {
        String sql = """
            SELECT COUNT(*) 
            FROM mesas 
            WHERE numero = :numero 
            AND deleted_at IS NULL
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("numero", numero);
        
        Long count = namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
        return count != null && count > 0;
    }

    public Boolean existsByNumeroExcludingId(Integer numero, Long id) {
        String sql = """
            SELECT COUNT(*) 
            FROM mesas 
            WHERE numero = :numero 
            AND id != :id
            AND deleted_at IS NULL
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("numero", numero);
        params.addValue("id", id);
        
        Long count = namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
        return count != null && count > 0;
    }

    public List<MesaDto> findAllActive() {
        String sql = """
            SELECT
                id,
                numero,
                estado,
                total_acumulado::text AS total_acumulado,
                activo,
                created_at,
                updated_at
            FROM mesas
            WHERE activo = 1 
            AND deleted_at IS NULL
            ORDER BY numero ASC
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        return MapperRepository.mapListToDtoList(resultList, MesaDto.class);
    }

    public List<MesaDto> findByEstado(String estado) {
        String sql = """
            SELECT
                id,
                numero,
                estado,
                total_acumulado::text AS total_acumulado,
                activo,
                created_at,
                updated_at
            FROM mesas
            WHERE estado = :estado
            AND activo = 1 
            AND deleted_at IS NULL
            ORDER BY numero ASC
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("estado", estado);
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        return MapperRepository.mapListToDtoList(resultList, MesaDto.class);
    }

    public PageImpl<MesaTableDto> listMesas(PageableDto<Object> pageableDto) {
        int pageNumber = pageableDto.getPage() != null ? pageableDto.getPage().intValue() : 0;
        int pageSize = pageableDto.getRows() != null ? pageableDto.getRows().intValue() : 10;
        String search = pageableDto.getSearch() != null ? pageableDto.getSearch().trim() : null;

        StringBuilder sql = new StringBuilder("""
            SELECT
                id,
                numero,
                estado,
                total_acumulado::text AS total_acumulado,
                activo,
                COUNT(*) OVER() AS total_rows
            FROM mesas
            WHERE deleted_at IS NULL
        """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (search != null && !search.isEmpty()) {
            sql.append(" AND (CAST(numero AS TEXT) ILIKE :search OR LOWER(estado) ILIKE :search)");
            params.addValue("search", "%" + search.toLowerCase() + "%");
        }

        // Validación de ORDER BY
        String orderBy = "numero";
        if (pageableDto.getOrder_by() != null && !pageableDto.getOrder_by().isEmpty()) {
            String[] validColumns = {"id", "numero", "estado", "total_acumulado", "activo"};
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
        
        List<MesaTableDto> result = MapperRepository.mapListToDtoList(resultList, MesaTableDto.class);
        long count = resultList.isEmpty() ? 0 : ((Number) resultList.get(0).get("total_rows")).longValue();
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);

        return new PageImpl<>(result, pageable, count);
    }
}