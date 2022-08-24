package ru.yandex.practicum.filmorate.storage.director.impl;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
@Primary
public class DirectorDBStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    public DirectorDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Director> create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("director_id");
        Long id = simpleJdbcInsert.executeAndReturnKey(toMapDirector(director)).longValue();
        return getDirector(id);
    }

    @Override
    public Optional<Director> getDirector(long id) {
        String sql = "select  d.DIRECTOR_ID, d.NAME " +
                "from directors as d" +
                " where d.DIRECTOR_ID = ?";

        SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sql, id);

        if (directorRows.next()) {
            Director director = new Director(directorRows.getLong("director_id"),
                    directorRows.getString("name")
            );
            return Optional.of(director);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Director> getAll() {
        String sql = "select DIRECTOR_ID,NAME from DIRECTORS";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    private Map<String, Object> toMapDirector(Director director) {
        return Map.of("name", director.getName());
    }

    private Director makeDirector(ResultSet directorRows) throws SQLException {
        return new Director(directorRows.getLong("director_id"),
                directorRows.getString("name"));
    }

    @Override
    public Optional<Director> update(Director director) {
        String sql = "update DIRECTORS set NAME  = ? where DIRECTOR_ID = ?";
        jdbcTemplate.update(sql,
                director.getName(),
                director.getId());
        return getDirector(director.getId());
    }

    @Override
    public void removeDirector(Long id) {
        String sqlRemoveDirector = "DELETE FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlRemoveDirector, id);
        String sqlRemoveDirectorFilm = "DELETE FROM FILM_DIRECTORS WHERE DIRECTOR_ID = ?";
        jdbcTemplate.update(sqlRemoveDirectorFilm, id);
    }
}
