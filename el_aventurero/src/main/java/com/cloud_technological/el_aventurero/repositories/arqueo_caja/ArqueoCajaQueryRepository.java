package com.cloud_technological.el_aventurero.repositories.arqueo_caja;

import java.math.BigDecimal;
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
import com.cloud_technological.el_aventurero.dto.movimiento_caja.ResumenArqueosDto;
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
        java.math.BigDecimal saldoEsperado = datos.getTotal_ingresos().subtract(datos.getTotal_egresos());
        datos.setSaldo_esperado(saldoEsperado);
        
        return datos;
    }
    public ResumenArqueosDto getResumenArqueos(LocalDate fechaInicio, LocalDate fechaFin) {
        String sql = """
            SELECT
                COUNT(*) AS total_dias,
                COUNT(CASE WHEN estado = 'CUADRADO' OR estado = 'AJUSTADO' THEN 1 END) AS arqueos_realizados,
                COUNT(CASE WHEN estado = 'CUADRADO' THEN 1 END) AS arqueos_cuadrados,
                COUNT(CASE WHEN estado = 'PENDIENTE' THEN 1 END) AS arqueos_pendientes,
                COUNT(CASE WHEN estado = 'AJUSTADO' THEN 1 END) AS arqueos_ajustados,
                COALESCE(SUM(diferencia), 0) AS total_diferencias,
                COALESCE(SUM(CASE WHEN diferencia > 0 THEN diferencia ELSE 0 END), 0) AS total_sobrantes,
                COALESCE(SUM(CASE WHEN diferencia < 0 THEN ABS(diferencia) ELSE 0 END), 0) AS total_faltantes
            FROM arqueo_caja
            WHERE fecha BETWEEN :fechaInicio AND :fechaFin
            AND deleted_at IS NULL
        """;
        
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("fechaInicio", fechaInicio);
        params.addValue("fechaFin", fechaFin);
        
        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );
        
        ResumenArqueosDto resumen = new ResumenArqueosDto();
        
        if (!resultList.isEmpty()) {
            Map<String, Object> row = resultList.get(0);
            
            // Calcular días en el rango
            long diasEnRango = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
            
            Long totalDias = (Long) row.get("total_dias");
            Long arqueosRealizados = (Long) row.get("arqueos_realizados");
            Long arqueosCuadrados = (Long) row.get("arqueos_cuadrados");
            Long arqueosPendientes = (Long) row.get("arqueos_pendientes");
            Long arqueosAjustados = (Long) row.get("arqueos_ajustados");
            
            resumen.setTotal_dias((int) diasEnRango);
            resumen.setArqueos_realizados(arqueosRealizados != null ? arqueosRealizados.intValue() : 0);
            resumen.setArqueos_cuadrados(arqueosCuadrados != null ? arqueosCuadrados.intValue() : 0);
            resumen.setArqueos_pendientes(arqueosPendientes != null ? arqueosPendientes.intValue() : 0);
            resumen.setArqueos_ajustados(arqueosAjustados != null ? arqueosAjustados.intValue() : 0);
            
            // Diferencias
            Object totalDiferenciasObj = row.get("total_diferencias");
            Object totalSobrantesObj = row.get("total_sobrantes");
            Object totalFaltantesObj = row.get("total_faltantes");
            
            BigDecimal totalDiferencias = totalDiferenciasObj != null ? new BigDecimal(totalDiferenciasObj.toString()) : BigDecimal.ZERO;
            BigDecimal totalSobrantes = totalSobrantesObj != null ? new BigDecimal(totalSobrantesObj.toString()) : BigDecimal.ZERO;
            BigDecimal totalFaltantes = totalFaltantesObj != null ? new BigDecimal(totalFaltantesObj.toString()) : BigDecimal.ZERO;
            
            resumen.setTotal_diferencias(totalDiferencias);
            resumen.setTotal_sobrantes(totalSobrantes);
            resumen.setTotal_faltantes(totalFaltantes);
        } else {
            // Si no hay datos, retornar valores por defecto
            long diasEnRango = java.time.temporal.ChronoUnit.DAYS.between(fechaInicio, fechaFin) + 1;
            
            resumen.setTotal_dias((int) diasEnRango);
            resumen.setArqueos_realizados(0);
            resumen.setArqueos_cuadrados(0);
            resumen.setArqueos_pendientes(0);
            resumen.setArqueos_ajustados(0);
            resumen.setTotal_diferencias(BigDecimal.ZERO);
            resumen.setTotal_sobrantes(BigDecimal.ZERO);
            resumen.setTotal_faltantes(BigDecimal.ZERO);
        }
        
        return resumen;
    }
}