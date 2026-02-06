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
import com.cloud_technological.el_aventurero.dto.conteo_inventario.ResumenConteosDto;
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
    public ResumenConteosDto getResumenConteos(LocalDate fechaInicio, LocalDate fechaFin) {
        // Query para obtener resumen de conteos
        String sqlConteos = """
            SELECT
                COUNT(*) AS total_conteos,
                COUNT(CASE WHEN estado = 'COMPLETADO' THEN 1 END) AS conteos_completados,
                COUNT(CASE WHEN estado = 'EN_PROCESO' THEN 1 END) AS conteos_en_proceso
            FROM conteo_inventario
            WHERE fecha BETWEEN :fechaInicio AND :fechaFin
            AND deleted_at IS NULL
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fechaInicio", fechaInicio);
        params.addValue("fechaFin", fechaFin);
        
        List<Map<String, Object>> resultConteos = namedParameterJdbcTemplate.query(
            sqlConteos, params, new ColumnMapRowMapper()
        );
        
        ResumenConteosDto resumen = new ResumenConteosDto();
        
        if (!resultConteos.isEmpty()) {
            Map<String, Object> row = resultConteos.get(0);
            
            Long totalConteos = (Long) row.get("total_conteos");
            Long conteosCompletados = (Long) row.get("conteos_completados");
            Long conteosEnProceso = (Long) row.get("conteos_en_proceso");
            
            resumen.setTotal_conteos(totalConteos != null ? totalConteos.intValue() : 0);
            resumen.setConteos_completados(conteosCompletados != null ? conteosCompletados.intValue() : 0);
            resumen.setConteos_en_proceso(conteosEnProceso != null ? conteosEnProceso.intValue() : 0);
            resumen.setConteo_realizado_semana(totalConteos != null && totalConteos > 0);
        } else {
            resumen.setTotal_conteos(0);
            resumen.setConteos_completados(0);
            resumen.setConteos_en_proceso(0);
            resumen.setConteo_realizado_semana(false);
        }
        
        // Query para productos contados y diferencias
        String sqlDetalles = """
            SELECT
                COUNT(DISTINCT dc.producto_id) AS total_productos,
                COUNT(CASE WHEN dc.diferencia != 0 THEN 1 END) AS total_diferencias,
                COUNT(CASE WHEN dc.ajustado = true THEN 1 END) AS total_ajustes
            FROM detalle_conteo dc
            INNER JOIN conteo_inventario ci ON ci.id = dc.conteo_id
            WHERE ci.fecha BETWEEN :fechaInicio AND :fechaFin
            AND ci.deleted_at IS NULL
        """;
        
        List<Map<String, Object>> resultDetalles = namedParameterJdbcTemplate.query(
            sqlDetalles, params, new ColumnMapRowMapper()
        );
        
        if (!resultDetalles.isEmpty()) {
            Map<String, Object> row = resultDetalles.get(0);
            
            Long totalProductos = (Long) row.get("total_productos");
            Long totalDiferencias = (Long) row.get("total_diferencias");
            Long totalAjustes = (Long) row.get("total_ajustes");
            
            resumen.setTotal_productos_contados(totalProductos != null ? totalProductos.intValue() : 0);
            resumen.setTotal_diferencias(totalDiferencias != null ? totalDiferencias.intValue() : 0);
            resumen.setTotal_ajustes(totalAjustes != null ? totalAjustes.intValue() : 0);
        } else {
            resumen.setTotal_productos_contados(0);
            resumen.setTotal_diferencias(0);
            resumen.setTotal_ajustes(0);
        }
        
        return resumen;
    }
}