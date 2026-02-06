package com.cloud_technological.el_aventurero.repositories.ajuste_inventario;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cloud_technological.el_aventurero.dto.conteo_inventario.AjusteInventarioDto;
import com.cloud_technological.el_aventurero.util.MapperRepository;

@Repository
public class AjusteInventarioQueryRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<AjusteInventarioDto> findByConteoId(Long conteoId) {
        String sql = """
                    SELECT
                        a.id,
                        a.producto_id,
                        p.nombre AS producto_nombre,
                        a.tipo,
                        a.cantidad,
                        a.motivo,
                        a.descripcion,
                        a.conteo_id,
                        a.usuario_id,
                        a.fecha,
                        a.created_at
                    FROM ajuste_inventario a
                    LEFT JOIN productos p ON p.id = a.producto_id
                    WHERE a.conteo_id = :conteoId
                    ORDER BY a.created_at DESC
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("conteoId", conteoId);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
                sql, params, new ColumnMapRowMapper());

        return MapperRepository.mapListToDtoList(resultList, AjusteInventarioDto.class);
    }

    public List<AjusteInventarioDto> findByProductoId(Long productoId) {
        String sql = """
                    SELECT
                        a.id,
                        a.producto_id,
                        p.nombre AS producto_nombre,
                        a.tipo,
                        a.cantidad,
                        a.motivo,
                        a.descripcion,
                        a.conteo_id,
                        a.usuario_id,
                        a.fecha,
                        a.created_at
                    FROM ajuste_inventario a
                    LEFT JOIN productos p ON p.id = a.producto_id
                    WHERE a.producto_id = :productoId
                    ORDER BY a.fecha DESC, a.created_at DESC
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("productoId", productoId);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
                sql, params, new ColumnMapRowMapper());

        return MapperRepository.mapListToDtoList(resultList, AjusteInventarioDto.class);
    }

    public List<AjusteInventarioDto> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        String sql = """
                    SELECT
                        a.id,
                        a.producto_id,
                        p.nombre AS producto_nombre,
                        a.tipo,
                        a.cantidad,
                        a.motivo,
                        a.descripcion,
                        a.conteo_id,
                        a.usuario_id,
                        a.fecha,
                        a.created_at
                    FROM ajuste_inventario a
                    LEFT JOIN productos p ON p.id = a.producto_id
                    WHERE a.fecha BETWEEN :fechaInicio AND :fechaFin
                    ORDER BY a.fecha DESC, a.created_at DESC
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fechaInicio", fechaInicio);
        params.addValue("fechaFin", fechaFin);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
                sql, params, new ColumnMapRowMapper());

        return MapperRepository.mapListToDtoList(resultList, AjusteInventarioDto.class);
    }

    public AjusteInventarioDto findById(Long id) {
        String sql = """
                    SELECT
                        a.id,
                        a.producto_id,
                        p.nombre AS producto_nombre,
                        a.tipo,
                        a.cantidad,
                        a.motivo,
                        a.descripcion,
                        a.conteo_id,
                        a.usuario_id,
                        a.fecha,
                        a.created_at
                    FROM ajuste_inventario a
                    LEFT JOIN productos p ON p.id = a.producto_id
                    WHERE a.id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
                sql, params, new ColumnMapRowMapper());

        if (resultList.isEmpty()) {
            return null;
        }

        List<AjusteInventarioDto> result = MapperRepository.mapListToDtoList(resultList, AjusteInventarioDto.class);
        return result.get(0);
    }
}