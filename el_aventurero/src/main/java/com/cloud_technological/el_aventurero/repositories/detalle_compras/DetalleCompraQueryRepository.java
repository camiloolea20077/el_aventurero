package com.cloud_technological.el_aventurero.repositories.detalle_compras;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cloud_technological.el_aventurero.dto.detalle_compras.DetalleCompraDto;
import com.cloud_technological.el_aventurero.util.MapperRepository;

@Repository
public class DetalleCompraQueryRepository {
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<DetalleCompraDto> findByCompraId(Long compraId) {
        String sql = """
            SELECT
                dc.id,
                dc.compra_id,
                dc.producto_id,
                p.nombre AS producto_nombre,
                dc.cajas,
                dc.unidades_por_caja,
                dc.total_unidades,
                dc.costo_total,
                dc.costo_unitario,
                dc.precio_sugerido,
                dc.precio_venta,
                dc.activo
            FROM detalle_compra dc
            INNER JOIN productos p ON p.id = dc.producto_id
            WHERE dc.compra_id = :compra_id
            AND dc.deleted_at IS NULL
            ORDER BY dc.id ASC
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("compra_id", compraId);
        
        return namedParameterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
            DetalleCompraDto dto = new DetalleCompraDto();
            dto.setId(rs.getLong("id"));
            dto.setCompra_id(rs.getLong("compra_id"));
            dto.setProducto_id(rs.getLong("producto_id"));
            dto.setProducto_nombre(rs.getString("producto_nombre"));
            dto.setCajas(rs.getInt("cajas"));
            dto.setUnidades_por_caja(rs.getInt("unidades_por_caja"));
            dto.setTotal_unidades(rs.getInt("total_unidades"));
            dto.setCosto_total(rs.getBigDecimal("costo_total"));
            dto.setCosto_unitario(rs.getBigDecimal("costo_unitario"));
            dto.setPrecio_sugerido(rs.getBigDecimal("precio_sugerido"));
            dto.setPrecio_venta(rs.getBigDecimal("precio_venta"));
            dto.setActivo(rs.getLong("activo"));
            return dto;
        });
    }
}