package com.cloud_technological.el_aventurero.repositories.conteo_inventario;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cloud_technological.el_aventurero.dto.conteo_inventario.ConteoInventarioDto;
import com.cloud_technological.el_aventurero.util.MapperRepository;

@Repository
public class ConteoInventarioQueryRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<ConteoInventarioDto> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        String sql = """
                    SELECT
                        c.id,
                        c.fecha,
                        c.tipo,
                        c.estado,
                        c.created_at,
                        c.updated_at
                    FROM conteo_inventario c
                    WHERE c.deleted_at IS NULL
                    AND c.fecha BETWEEN :fechaInicio AND :fechaFin
                    ORDER BY c.fecha DESC, c.created_at DESC
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fechaInicio", fechaInicio);
        params.addValue("fechaFin", fechaFin);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
                sql, params, new ColumnMapRowMapper());

        return MapperRepository.mapListToDtoList(resultList, ConteoInventarioDto.class);
    }

    public ConteoInventarioDto findById(Long id) {
        String sql = """
                    SELECT
                        c.id,
                        c.fecha,
                        c.tipo,
                        c.estado,
                        c.created_at,
                        c.updated_at
                    FROM conteo_inventario c
                    WHERE c.id = :id
                    AND c.deleted_at IS NULL
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
                sql, params, new ColumnMapRowMapper());

        if (resultList.isEmpty()) {
            return null;
        }

        List<ConteoInventarioDto> result = MapperRepository.mapListToDtoList(resultList, ConteoInventarioDto.class);
        return result.get(0);
    }

    public ConteoInventarioDto findLastConteo() {
        String sql = """
                    SELECT
                        c.id,
                        c.fecha,
                        c.tipo,
                        c.estado,
                        c.created_at,
                        c.updated_at
                    FROM conteo_inventario c
                    WHERE c.deleted_at IS NULL
                    ORDER BY c.fecha DESC, c.created_at DESC
                    LIMIT 1
                """;

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
                sql, new MapSqlParameterSource(), new ColumnMapRowMapper());

        if (resultList.isEmpty()) {
            return null;
        }

        List<ConteoInventarioDto> result = MapperRepository.mapListToDtoList(resultList, ConteoInventarioDto.class);
        return result.get(0);
    }
}