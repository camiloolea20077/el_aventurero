package com.cloud_technological.el_aventurero.repositories.users;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.cloud_technological.el_aventurero.dto.users.UsersTableDto;
import com.cloud_technological.el_aventurero.util.MapperRepository;
import com.cloud_technological.el_aventurero.util.PageableDto;


@Repository
public class UserQueryRepository {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = :email";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
    
        Long count = namedParameterJdbcTemplate.queryForObject(sql, params, Long.class);
        return count != null && count > 0;
    }
    public PageImpl<UsersTableDto> listUsers(PageableDto<Object> pageableDto) {
        int pageNumber = pageableDto.getPage() != null ? pageableDto.getPage().intValue() : 0;
        int pageSize = pageableDto.getRows() != null ? pageableDto.getRows().intValue() : 10;
        String search = pageableDto.getSearch() != null ? pageableDto.getSearch().trim() : null;

        StringBuilder sql;
        sql = new StringBuilder("""
                                    SELECT
                                        o.id,
                                        o.nombre AS name,
                                        o.email AS email,
                                        r.nombre AS role,
                                        o.activo as activo,
                                        COUNT(*) OVER() AS total_rows
                                    FROM usuarios  o
                                    LEFT JOIN roles r ON r.id = o.rol_id
                                    WHERE
                                        o.deleted_at IS NULL
                                        AND o.activo = 1
                                """);

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (search != null && !search.isEmpty()) {
            sql.append("""
                AND (
                    LOWER(o.nombre_completo) ILIKE :search
                    OR LOWER(o.email) ILIKE :search
                )
            """);
            params.addValue("search", "%" + search.toLowerCase() + "%");
        }

        if (pageableDto.getOrder_by() != null && !pageableDto.getOrder_by().isEmpty()) {
            sql.append(" ORDER BY ").append(pageableDto.getOrder_by()).append(" ").append(pageableDto.getOrder()).append(" ");
        } else {
            sql.append(" ORDER BY o.id ASC ");
        }

        sql.append(" OFFSET :offset LIMIT :limit");
        long offset = pageNumber * pageSize;
        params.addValue("offset", offset);
        params.addValue("limit", pageSize);

        List<Map<String, Object>> resultList = namedParameterJdbcTemplate.query(sql.toString(), params, new ColumnMapRowMapper());
        List<UsersTableDto> result = MapperRepository.mapListToDtoList(resultList, UsersTableDto.class);

        long count = resultList.isEmpty() ? 0 : ((Number) resultList.get(0).get("total_rows")).longValue();
        PageRequest pageable = PageRequest.of(pageNumber, pageSize);

        return new PageImpl<>(result, pageable, count);
    }
}
