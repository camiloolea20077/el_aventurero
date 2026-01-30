package com.cloud_technological.el_aventurero.repositories.role;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.management.relation.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;



@Repository
public class RoleQueryRepository {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

        public Optional<Role> findByName(String name)  {
        String sql = "SELECT id, name, description, created_at, updated_at, deleted_at FROM roles WHERE name = :name";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);
    
        List<Role> roles = namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(Role.class));
        return roles.stream().findFirst();
    }
    public Boolean existsByName(String name) {
        String sql = "SELECT COUNT(*) FROM roles WHERE name = :name";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", name);

        Long count = namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
        return count != null && count > 0;
    }
}
