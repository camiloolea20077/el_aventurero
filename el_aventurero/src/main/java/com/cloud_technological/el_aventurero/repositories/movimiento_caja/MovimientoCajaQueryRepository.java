package com.cloud_technological.el_aventurero.repositories.movimiento_caja;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cloud_technological.el_aventurero.dto.movimiento_caja.CierreSemanalDto;
import com.cloud_technological.el_aventurero.dto.movimiento_caja.MetodoPagoResumenDto;
import com.cloud_technological.el_aventurero.dto.movimiento_caja.MovimientoCajaDto;
import com.cloud_technological.el_aventurero.dto.movimiento_caja.ProductoTopResumenDto;
import com.cloud_technological.el_aventurero.dto.movimiento_caja.ResumenFlujoDto;
import com.cloud_technological.el_aventurero.util.MapperRepository;

@Repository
public class MovimientoCajaQueryRepository {
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<MovimientoCajaDto> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        String sql = """
            SELECT
                m.id,
                m.tipo,
                m.concepto,
                m.categoria,
                m.monto::text AS monto,
                m.metodo_pago,
                m.descripcion,
                m.fecha,
                m.venta_id,
                m.compra_id,
                m.activo,
                m.created_at,
                m.updated_at
            FROM movimiento_caja m
            WHERE m.deleted_at IS NULL
            AND m.fecha BETWEEN :fechaInicio AND :fechaFin
            ORDER BY m.fecha DESC, m.created_at DESC
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fechaInicio", fechaInicio);
        params.addValue("fechaFin", fechaFin);
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        return MapperRepository.mapListToDtoListNull(resultList, MovimientoCajaDto.class);
    }

    public ResumenFlujoDto getResumenFlujo(LocalDate fechaInicio, LocalDate fechaFin) {
        String sql = """
            SELECT
                :fechaInicio AS fecha_inicio,
                :fechaFin AS fecha_fin,
                COALESCE(SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.monto ELSE 0 END), 0)::text AS total_ingresos,
                COALESCE(SUM(CASE WHEN m.tipo = 'EGRESO' THEN m.monto ELSE 0 END), 0)::text AS total_egresos,
                COALESCE(SUM(CASE WHEN m.tipo = 'INGRESO' THEN m.monto ELSE -m.monto END), 0)::text AS balance,
                COUNT(CASE WHEN m.tipo = 'INGRESO' THEN 1 END) AS movimientos_ingreso,
                COUNT(CASE WHEN m.tipo = 'EGRESO' THEN 1 END) AS movimientos_egreso
            FROM movimiento_caja m
            WHERE m.deleted_at IS NULL
            AND m.fecha BETWEEN :fechaInicio AND :fechaFin
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fechaInicio", fechaInicio);
        params.addValue("fechaFin", fechaFin);
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        if (resultList.isEmpty()) {
            ResumenFlujoDto resumen = new ResumenFlujoDto();
            resumen.setFecha_inicio(fechaInicio);
            resumen.setFecha_fin(fechaFin);
            resumen.setTotal_ingresos(BigDecimal.ZERO);
            resumen.setTotal_egresos(BigDecimal.ZERO);
            resumen.setBalance(BigDecimal.ZERO);
            resumen.setMovimientos_ingreso(0L);
            resumen.setMovimientos_egreso(0L);
            return resumen;
        }
                
        List<ResumenFlujoDto> result = MapperRepository.mapListToDtoListNull(resultList, ResumenFlujoDto.class);
        return result.get(0);
    }

    public MovimientoCajaDto findById(Long id) {
        String sql = """
            SELECT
                m.id,
                m.tipo,
                m.concepto,
                m.categoria,
                m.monto::text AS monto,
                m.metodo_pago,
                m.descripcion,
                m.fecha,
                m.venta_id,
                m.compra_id,
                m.activo,
                m.created_at,
                m.updated_at
            FROM movimiento_caja m
            WHERE m.id = :id
            AND m.deleted_at IS NULL
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        if (resultList.isEmpty()) {
            return null;
        }
        
        List<MovimientoCajaDto> result = MapperRepository.mapListToDtoListNull(resultList, MovimientoCajaDto.class);
        return result.get(0);
    }
    public CierreSemanalDto getCierreSemanal(LocalDate fechaInicio, LocalDate fechaFin) {
    // 1. Obtener datos generales de ventas
        String sqlVentas = """
            SELECT
                COALESCE(SUM(v.total), 0)::text AS ventas_totales,
                COUNT(v.id) AS cantidad_ventas,
                CASE 
                    WHEN COUNT(v.id) > 0 THEN (SUM(v.total) / COUNT(v.id))::text
                    ELSE '0'
                END AS ticket_promedio
            FROM ventas v
            WHERE v.deleted_at IS NULL
            AND v.activo = 1
            AND DATE(v.created_at) BETWEEN :fechaInicio AND :fechaFin
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fechaInicio", fechaInicio);
        params.addValue("fechaFin", fechaFin);
        
        List<Map<String, Object>> ventasResult = namedParameterJdbcTemplate.query(
            sqlVentas, params, new ColumnMapRowMapper()
        );
        
        Map<String, Object> ventasData = ventasResult.isEmpty() ? null : ventasResult.get(0);
        
        // 2. Obtener resumen de flujo (ingresos/egresos)
        ResumenFlujoDto resumenFlujo = getResumenFlujo(fechaInicio, fechaFin);
        
        // 3. Obtener métodos de pago
        String sqlMetodosPago = """
            SELECT
                v.metodo_pago AS metodo,
                COUNT(v.id) AS cantidad,
                COALESCE(SUM(v.total), 0)::text AS total
            FROM ventas v
            WHERE v.deleted_at IS NULL
            AND v.activo = 1
            AND DATE(v.created_at) BETWEEN :fechaInicio AND :fechaFin
            GROUP BY v.metodo_pago
            ORDER BY SUM(v.total) DESC
        """;
        
        List<Map<String, Object>> metodosPagoResult = namedParameterJdbcTemplate.query(
            sqlMetodosPago, params, new ColumnMapRowMapper()
        );
        
        List<MetodoPagoResumenDto> metodosPago = MapperRepository.mapListToDtoListNull(
            metodosPagoResult, 
            MetodoPagoResumenDto.class
        );
        
        // Calcular porcentajes
        BigDecimal totalVentas = ventasData != null ? 
            new BigDecimal(ventasData.get("ventas_totales").toString()) : BigDecimal.ZERO;
        
        if (totalVentas.compareTo(BigDecimal.ZERO) > 0) {
            for (MetodoPagoResumenDto metodo : metodosPago) {
                BigDecimal porcentaje = metodo.getTotal()
                    .multiply(new BigDecimal("100"))
                    .divide(totalVentas, 2, BigDecimal.ROUND_HALF_UP);
                metodo.setPorcentaje(porcentaje);
            }
        }
        
        // 4. Obtener top 5 productos más vendidos
        String sqlProductosTop = """
            SELECT
                p.nombre AS producto_nombre,
                SUM(dv.cantidad) AS cantidad_vendida,
                COALESCE(SUM(dv.subtotal), 0)::text AS total_vendido
            FROM detalle_venta dv
            INNER JOIN ventas v ON v.id = dv.venta_id
            INNER JOIN productos p ON p.id = dv.producto_id
            WHERE v.deleted_at IS NULL
            AND v.activo = 1
            AND dv.deleted_at IS NULL
            AND DATE(v.created_at) BETWEEN :fechaInicio AND :fechaFin
            GROUP BY p.id, p.nombre
            ORDER BY SUM(dv.cantidad) DESC
            LIMIT 5
        """;
        
        List<Map<String, Object>> productosTopResult = namedParameterJdbcTemplate.query(
            sqlProductosTop, params, new ColumnMapRowMapper()
        );
        
        List<ProductoTopResumenDto> productosTop = MapperRepository.mapListToDtoListNull(
            productosTopResult, 
            ProductoTopResumenDto.class
        );
        
        // 5. Construir el DTO de cierre semanal
        CierreSemanalDto cierre = new CierreSemanalDto();
        cierre.setSemana(getNumeroSemana(fechaInicio));
        cierre.setFecha_inicio(fechaInicio);
        cierre.setFecha_fin(fechaFin);
        
        if (ventasData != null) {
            cierre.setVentas_totales(new BigDecimal(ventasData.get("ventas_totales").toString()));
            cierre.setCantidad_ventas(((Number) ventasData.get("cantidad_ventas")).longValue());
            cierre.setTicket_promedio(new BigDecimal(ventasData.get("ticket_promedio").toString()));
        } else {
            cierre.setVentas_totales(BigDecimal.ZERO);
            cierre.setCantidad_ventas(0L);
            cierre.setTicket_promedio(BigDecimal.ZERO);
        }
        
        cierre.setTotal_ingresos(resumenFlujo.getTotal_ingresos());
        cierre.setTotal_egresos(resumenFlujo.getTotal_egresos());
        cierre.setBalance_neto(resumenFlujo.getBalance());
        cierre.setMetodos_pago(metodosPago);
        cierre.setProductos_top(productosTop);
        
        return cierre;
    }

    private Integer getNumeroSemana(LocalDate fecha) {
        // Calcular número de semana del año
        java.time.temporal.WeekFields weekFields = java.time.temporal.WeekFields.of(java.util.Locale.getDefault());
        return fecha.get(weekFields.weekOfWeekBasedYear());
    }
}