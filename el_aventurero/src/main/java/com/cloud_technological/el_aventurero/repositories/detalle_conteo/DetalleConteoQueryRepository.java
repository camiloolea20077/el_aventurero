package com.cloud_technological.el_aventurero.repositories.detalle_conteo;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cloud_technological.el_aventurero.dto.conteo_inventario.DetalleConteoDto;
import com.cloud_technological.el_aventurero.util.MapperRepository;

@Repository
public class DetalleConteoQueryRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<DetalleConteoDto> findByConteoId(Long conteoId) {
        String sql = """
                    SELECT
                        dc.id,
                        dc.conteo_id,
                        dc.producto_id,
                        p.nombre AS producto_nombre,
                        dc.stock_sistema,
                        dc.stock_fisico,
                        dc.diferencia,
                        dc.motivo,
                        dc.ajustado,
                        dc.created_at
                    FROM detalle_conteo dc
                    LEFT JOIN productos p ON p.id = dc.producto_id
                    WHERE dc.conteo_id = :conteoId
                    ORDER BY p.nombre ASC
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("conteoId", conteoId);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
                sql, params, new ColumnMapRowMapper());

        return MapperRepository.mapListToDtoList(resultList, DetalleConteoDto.class);
    }

    public DetalleConteoDto findById(Long id) {
        String sql = """
                    SELECT
                        dc.id,
                        dc.conteo_id,
                        dc.producto_id,
                        p.nombre AS producto_nombre,
                        dc.stock_sistema,
                        dc.stock_fisico,
                        dc.diferencia,
                        dc.motivo,
                        dc.ajustado,
                        dc.created_at
                    FROM detalle_conteo dc
                    LEFT JOIN productos p ON p.id = dc.producto_id
                    WHERE dc.id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
                sql, params, new ColumnMapRowMapper());

        if (resultList.isEmpty()) {
            return null;
        }

        List<DetalleConteoDto> result = MapperRepository.mapListToDtoList(resultList, DetalleConteoDto.class);
        return result.get(0);
    }

    public List<DetalleConteoDto> findDiferencias(Long conteoId) {
        String sql = """
                    SELECT
                        dc.id,
                        dc.conteo_id,
                        dc.producto_id,
                        p.nombre AS producto_nombre,
                        dc.stock_sistema,
                        dc.stock_fisico,
                        dc.diferencia,
                        dc.motivo,
                        dc.ajustado,
                        dc.created_at
                    FROM detalle_conteo dc
                    LEFT JOIN productos p ON p.id = dc.producto_id
                    WHERE dc.conteo_id = :conteoId
                    AND dc.diferencia != 0
                    ORDER BY ABS(dc.diferencia) DESC
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("conteoId", conteoId);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
                sql, params, new ColumnMapRowMapper());

        return MapperRepository.mapListToDtoList(resultList, DetalleConteoDto.class);
    }

    public List<DetalleConteoDto> findPendientesAjuste(Long conteoId) {
        String sql = """
                    SELECT
                        dc.id,
                        dc.conteo_id,
                        dc.producto_id,
                        p.nombre AS producto_nombre,
                        dc.stock_sistema,
                        dc.stock_fisico,
                        dc.diferencia,
                        dc.motivo,
                        dc.ajustado,
                        dc.created_at
                    FROM detalle_conteo dc
                    LEFT JOIN productos p ON p.id = dc.producto_id
                    WHERE dc.conteo_id = :conteoId
                    AND dc.ajustado = FALSE
                    AND dc.diferencia != 0
                    ORDER BY ABS(dc.diferencia) DESC
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("conteoId", conteoId);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
                sql, params, new ColumnMapRowMapper());

        return MapperRepository.mapListToDtoList(resultList, DetalleConteoDto.class);
    }
}