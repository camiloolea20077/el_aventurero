package com.cloud_technological.el_aventurero.repositories.arqueo_caja;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cloud_technological.el_aventurero.dto.arqueo_caja.ArqueoCajaDto;
import com.cloud_technological.el_aventurero.dto.arqueo_caja.DatosParaArqueoDto;
import com.cloud_technological.el_aventurero.util.MapperRepository;

@Repository
public class ArqueoCajaQueryRepository {
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<ArqueoCajaDto> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin) {
        String sql = """
            SELECT
                a.id,
                a.fecha,
                a.saldo_inicial::text AS saldo_inicial,
                a.total_ingresos_sistema::text AS total_ingresos_sistema,
                a.total_egresos_sistema::text AS total_egresos_sistema,
                a.saldo_esperado::text AS saldo_esperado,
                a.efectivo_real::text AS efectivo_real,
                a.diferencia::text AS diferencia,
                a.estado,
                a.observaciones,
                a.billetes_100000,
                a.billetes_50000,
                a.billetes_20000,
                a.billetes_10000,
                a.billetes_5000,
                a.billetes_2000,
                a.billetes_1000,
                a.monedas_1000,
                a.monedas_500,
                a.monedas_200,
                a.monedas_100,
                a.monedas_50,
                a.activo,
                a.created_at,
                a.updated_at
            FROM arqueo_caja a
            WHERE a.deleted_at IS NULL
            AND a.fecha BETWEEN :fechaInicio AND :fechaFin
            ORDER BY a.fecha DESC, a.created_at DESC
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fechaInicio", fechaInicio);
        params.addValue("fechaFin", fechaFin);
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        return MapperRepository.mapListToDtoListNull(resultList, ArqueoCajaDto.class);
    }

    public ArqueoCajaDto findById(Long id) {
        String sql = """
            SELECT
                a.id,
                a.fecha,
                a.saldo_inicial::text AS saldo_inicial,
                a.total_ingresos_sistema::text AS total_ingresos_sistema,
                a.total_egresos_sistema::text AS total_egresos_sistema,
                a.saldo_esperado::text AS saldo_esperado,
                a.efectivo_real::text AS efectivo_real,
                a.diferencia::text AS diferencia,
                a.estado,
                a.observaciones,
                a.billetes_100000,
                a.billetes_50000,
                a.billetes_20000,
                a.billetes_10000,
                a.billetes_5000,
                a.billetes_2000,
                a.billetes_1000,
                a.monedas_1000,
                a.monedas_500,
                a.monedas_200,
                a.monedas_100,
                a.monedas_50,
                a.activo,
                a.created_at,
                a.updated_at
            FROM arqueo_caja a
            WHERE a.id = :id
            AND a.deleted_at IS NULL
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        if (resultList.isEmpty()) {
            return null;
        }
        
        List<ArqueoCajaDto> result = MapperRepository.mapListToDtoListNull(resultList, ArqueoCajaDto.class);
        return result.get(0);
    }

    public ArqueoCajaDto findByFecha(LocalDate fecha) {
        String sql = """
            SELECT
                a.id,
                a.fecha,
                a.saldo_inicial::text AS saldo_inicial,
                a.total_ingresos_sistema::text AS total_ingresos_sistema,
                a.total_egresos_sistema::text AS total_egresos_sistema,
                a.saldo_esperado::text AS saldo_esperado,
                a.efectivo_real::text AS efectivo_real,
                a.diferencia::text AS diferencia,
                a.estado,
                a.observaciones,
                a.billetes_100000,
                a.billetes_50000,
                a.billetes_20000,
                a.billetes_10000,
                a.billetes_5000,
                a.billetes_2000,
                a.billetes_1000,
                a.monedas_1000,
                a.monedas_500,
                a.monedas_200,
                a.monedas_100,
                a.monedas_50,
                a.activo,
                a.created_at,
                a.updated_at
            FROM arqueo_caja a
            WHERE a.deleted_at IS NULL
            AND a.fecha = :fecha
            ORDER BY a.created_at DESC
            LIMIT 1
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fecha", fecha);
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        if (resultList.isEmpty()) {
            return null;
        }
        
        List<ArqueoCajaDto> result = MapperRepository.mapListToDtoListNull(resultList, ArqueoCajaDto.class);
        return result.get(0);
    }

    public DatosParaArqueoDto getDatosParaArqueo(LocalDate fecha) {
        String sql = """
            SELECT
                COALESCE(
                    (SELECT a.efectivo_real 
                     FROM arqueo_caja a 
                     WHERE a.deleted_at IS NULL 
                     AND a.fecha < :fecha 
                     ORDER BY a.fecha DESC, a.created_at DESC 
                     LIMIT 1), 
                    0
                )::text AS saldo_inicial,
                COALESCE(
                    (SELECT SUM(m.monto) 
                     FROM movimiento_caja m 
                     WHERE m.deleted_at IS NULL 
                     AND m.tipo = 'INGRESO' 
                     AND m.fecha = :fecha), 
                    0
                )::text AS total_ingresos,
                COALESCE(
                    (SELECT SUM(m.monto) 
                     FROM movimiento_caja m 
                     WHERE m.deleted_at IS NULL 
                     AND m.tipo = 'EGRESO' 
                     AND m.fecha = :fecha), 
                    0
                )::text AS total_egresos
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fecha", fecha);
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        if (resultList.isEmpty()) {
            DatosParaArqueoDto datos = new DatosParaArqueoDto();
            datos.setSaldo_inicial(java.math.BigDecimal.ZERO);
            datos.setTotal_ingresos(java.math.BigDecimal.ZERO);
            datos.setTotal_egresos(java.math.BigDecimal.ZERO);
            datos.setSaldo_esperado(java.math.BigDecimal.ZERO);
            return datos;
        }
        
        List<DatosParaArqueoDto> result = MapperRepository.mapListToDtoListNull(resultList, DatosParaArqueoDto.class);
        DatosParaArqueoDto datos = result.get(0);
        
        // Calcular saldo esperado
        java.math.BigDecimal saldoEsperado = datos.getSaldo_inicial()
            .add(datos.getTotal_ingresos())
            .subtract(datos.getTotal_egresos());
        datos.setSaldo_esperado(saldoEsperado);
        
        return datos;
    }
}