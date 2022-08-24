package ru.yandex.practicum.filmorate.storage.mpa.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@Primary
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MPA> getAllMpa() {
        String sql = "select mpa.RATING_MPA_ID,mpa.NAME from rating_MPA mpa";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    private MPA makeMpa(ResultSet mpaRows) throws SQLException {
        return new MPA(mpaRows.getLong("rating_mpa_id"),
                mpaRows.getString("name"));
    }

    @Override
    public Optional<MPA> getMpa(Long id) {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("select RATING_MPA_ID,NAME from rating_MPA where rating_mpa_id = ?", id);

        if (mpaRows.next()) {
            MPA mpa = new MPA(mpaRows.getLong("rating_mpa_id"),
                    mpaRows.getString("name")
            );
            return Optional.of(mpa);
        } else {
            return Optional.empty();
        }
    }
}
