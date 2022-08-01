package ru.yandex.practicum.filmorate.storage.mpa.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Primary
public class MpaDbStore implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MPA> getAllMpa() {
        String sql = "select * from rating_MPA";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    private MPA makeMpa(ResultSet mpaRows) throws SQLException {
        return new MPA(mpaRows.getLong("rating_mpa_id"),
                mpaRows.getString("name"));
    }

    @Override
    public Optional<MPA> getMpa(Long id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select * from rating_MPA where rating_mpa_id = ?", id);

        if (mpaRows.next()) {
            log.info("Найден Рейтинг: {} {}", mpaRows.getString("rating_mpa_id"), mpaRows.getString("name"));
            MPA mpa = new MPA(mpaRows.getLong("rating_mpa_id"),
                    mpaRows.getString("name")
            );
            return Optional.of(mpa);
        } else {
            log.info("Рейтинг с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }
}
