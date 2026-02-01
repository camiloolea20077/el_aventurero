package com.cloud_technological.el_aventurero.repositories.detalle_venta;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cloud_technological.el_aventurero.dto.detalle_venta.DetalleVentaDto;
import com.cloud_technological.el_aventurero.util.MapperRepository;

@Repository
public class DetalleVentaQueryRepository {
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<DetalleVentaDto> findByVentaId(Long ventaId) {
        String sql = """
            SELECT
                dv.id,
                dv.venta_id,
                dv.producto_id,
                p.nombre AS producto_nombre,
                dv.cantidad,
                dv.precio_unitario::text AS precio_unitario,
                dv.subtotal::text AS subtotal,
                dv.activo
            FROM detalle_venta dv
            INNER JOIN productos p ON p.id = dv.producto_id
            WHERE dv.venta_id = :venta_id
            AND dv.deleted_at IS NULL
            ORDER BY dv.id ASC
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("venta_id", ventaId);
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        return MapperRepository.mapListToDtoListNull(resultList, DetalleVentaDto.class);
    }
}