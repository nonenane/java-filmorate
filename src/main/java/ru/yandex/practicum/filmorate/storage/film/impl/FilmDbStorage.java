package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAllFilms() {

        String sql = "select  film_id, f.name as fname, description, releaseDate, duration, " +
                "f.RATING_MPA_ID, rm.name as mpa_name " +
                "from films as f join rating_mpa as rm on f.RATING_MPA_ID = rm.RATING_MPA_ID";

        String sql_film = "select fg.FILM_ID,g.GENRE_ID,g.NAME " +
                "from film_genres fg join GENRES g on g.GENRE_ID = fg.GENRE_ID";

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql_film);
        Map<Long, Set<Genre>> setMap = makeGenreMap(genreRows);

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs, setMap));
        return films;
    }

    @Override
    public Optional<Film> getFilm(Long id) {
        String sql = "select  f.film_id, f.name, description, releaseDate, duration, " +
                "rm.RATING_MPA_ID, rm.name as mpa_name " +
                "from films as f join rating_mpa as rm on f.rating_mpa_id = rm.RATING_MPA_ID" +
                " where f.film_id = ?";


        String sql_film = "select fg.FILM_ID,g.GENRE_ID,g.NAME " +
                "from film_genres fg join GENRES g on g.GENRE_ID = fg.GENRE_ID " +
                "WHERE fg.FILM_ID = ?";

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql_film, id);
        Map<Long, Set<Genre>> setMap = makeGenreMap(genreRows);

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);

        if (filmRows.next()) {
            log.info("Найден фильм: {} {}", filmRows.getString("film_id"), filmRows.getString("NAME"));
            Film film = new Film(filmRows.getLong("film_id"),
                    filmRows.getString("NAME"),
                    filmRows.getString("description"),
                    filmRows.getDate("releaseDate").toLocalDate(),
                    filmRows.getInt("duration"),
                    new MPA(filmRows.getLong("RATING_MPA_ID"), filmRows.getString("MPA_NAME")),
                    setMap.getOrDefault(id, new HashSet<>())
            );
            return Optional.of(film);
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Film> create(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Long id = simpleJdbcInsert.executeAndReturnKey(toMapFilm(film)).longValue();

        if (film.getGenres() != null) {
            String sqlInsertFilmGenre = "INSERT INTO film_genres (FILM_ID,GENRE_ID) values (?,?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlInsertFilmGenre, id, genre.getId());
            }
        }

        return getFilm(id);
    }

    @Override
    public Optional<Film> update(Film film) {

        String sql = "update FILMS set NAME  = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ?,RATING_MPA_ID = ? where FILM_ID = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());

        String sqlDelGengerFilm = "DELETE FROM film_genres WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlDelGengerFilm, film.getId());

        if (film.getGenres() != null) {
            String sqlInsertFilmGenre = "INSERT INTO film_genres (FILM_ID,GENRE_ID) values (?,?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlInsertFilmGenre, film.getId(), genre.getId());
            }
        }

        return getFilm(film.getId());
    }

    private Film makeFilm(ResultSet filmRows, Map<Long, Set<Genre>> setMap) throws SQLException {
        Long film_id = filmRows.getLong("film_id");
        return new Film(film_id,
                filmRows.getString("fname"),
                filmRows.getString("description"),
                filmRows.getDate("releaseDate").toLocalDate(),
                filmRows.getInt("duration"),
                new MPA(filmRows.getLong("RATING_MPA_ID"), filmRows.getString("mpa_name")),
                setMap.getOrDefault(film_id, new HashSet<>()));
    }

    private Map<String, Object> toMapFilm(Film film) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("description", film.getDescription());
        values.put("releaseDate", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("RATING_MPA_ID", film.getMpa().getId());
        return values;
    }

    private Map<Long, Set<Genre>> makeGenreMap(SqlRowSet genresRowSet) {
        Map<Long, Set<Genre>> genreMap = new HashMap<>();
        while (genresRowSet.next()) {
            Long film_id = genresRowSet.getLong("film_id");
            Long genre_id = genresRowSet.getLong("GENRE_ID");
            String genre_name = genresRowSet.getString("NAME");

            if (genreMap.containsKey(film_id)) {
                genreMap.get(film_id).add(new Genre(genre_id, genre_name));
            } else {
                genreMap.put(film_id, new HashSet<>());
                genreMap.get(film_id).add(new Genre(genre_id, genre_name));
            }
        }
        return genreMap;
    }

    @Override
    public List<Film> getPopularFilm(Integer count) {

        String sql = "select  film_id, f.name as fname, description, releaseDate, duration, " +
                "f.RATING_MPA_ID, rm.name as mpa_name " +
                "from films as f join rating_mpa as rm on f.RATING_MPA_ID = rm.RATING_MPA_ID " +
                "ORDER BY f.LIKES_COUNTER DESC " +
                "LIMIT ?";

        String sql_film = "select fg.FILM_ID,g.GENRE_ID,g.NAME " +
                "from film_genres fg join GENRES g on g.GENRE_ID = fg.GENRE_ID";

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql_film);
        Map<Long, Set<Genre>> setMap = makeGenreMap(genreRows);

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs, setMap), count);
        return films;
    }
}
