package com.cloud_technological.el_aventurero.repositories.role;

import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cloud_technological.el_aventurero.dto.roles.RoleDto;
import com.cloud_technological.el_aventurero.util.MapperRepository;


@Repository
public class RoleQueryRepository {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Optional<RoleDto> findByNombre(String nombre) {
        String sql = """
            SELECT
                id,
                nombre AS name,
                descripcion AS description,
                activo AS active
            FROM roles
            WHERE deleted_at IS NULL
            AND LOWER(nombre) = LOWER(:nombre)
            LIMIT 1
        """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nombre", nombre);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );

        if (resultList.isEmpty()) {
            return Optional.empty();
        }

        List<RoleDto> result = MapperRepository.mapListToDtoListNull(resultList, RoleDto.class);
        return result.stream().findFirst();
    }

    public RoleDto findById(Long id) {
        String sql = """
            SELECT
                id,
                nombre AS name,
                descripcion AS description,
                activo AS active
            FROM roles
            WHERE id = :id
            AND deleted_at IS NULL
        """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, params, new ColumnMapRowMapper()
        );

        if (resultList.isEmpty()) {
            return null;
        }

        List<RoleDto> result = MapperRepository.mapListToDtoListNull(resultList, RoleDto.class);
        return result.get(0);
    }

    public Boolean existsByNombre(String nombre) {
        String sql = """
            SELECT COUNT(*)
            FROM roles
            WHERE deleted_at IS NULL
            AND LOWER(nombre) = LOWER(:nombre)
        """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nombre", nombre);

        Long count = namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
        return count != null && count > 0;
    }

    public Boolean existsByNombreExcludingId(String nombre, Long id) {
        String sql = """
            SELECT COUNT(*)
            FROM roles
            WHERE deleted_at IS NULL
            AND LOWER(nombre) = LOWER(:nombre)
            AND id != :id
        """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nombre", nombre);
        params.addValue("id", id);

        Long count = namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
        return count != null && count > 0;
    }

    public List<RoleDto> findAll() {
        String sql = """
            SELECT
                id,
                nombre AS name,
                descripcion AS description,
                activo AS active
            FROM roles
            WHERE deleted_at IS NULL
            ORDER BY nombre ASC
        """;

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, new MapSqlParameterSource(), new ColumnMapRowMapper()
        );

        return MapperRepository.mapListToDtoListNull(resultList, RoleDto.class);
    }

    public List<RoleDto> findAllActive() {
        String sql = """
            SELECT
                id,
                nombre AS name,
                descripcion AS description,
                activo AS active
            FROM roles
            WHERE activo = 1
            AND deleted_at IS NULL
            ORDER BY nombre ASC
        """;

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(
            sql, new MapSqlParameterSource(), new ColumnMapRowMapper()
        );

        return MapperRepository.mapListToDtoListNull(resultList, RoleDto.class);
    }
}
