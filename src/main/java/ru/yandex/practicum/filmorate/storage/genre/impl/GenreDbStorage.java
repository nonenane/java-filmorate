package ru.yandex.practicum.filmorate.storage.genre.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Primary
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAll() {
        String sql = "select GENRE_ID,NAME from genres";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    private Genre makeGenre(ResultSet genreRows) throws SQLException {
        return new Genre(genreRows.getLong("genre_id"),
                genreRows.getString("name"));
    }

    @Override
    public Optional<Genre> getGenre(Long id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("select * from genres where genre_id = ?", id);

        if (genreRows.next()) {
            log.info("Найден жанр: {} {}", genreRows.getString("genre_id"), genreRows.getString("name"));
            Genre mpa = new Genre(genreRows.getLong("genre_id"),
                    genreRows.getString("name")
            );
            return Optional.of(mpa);
        } else {
            log.info("Жанр с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }
}
