package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
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

        String sql_director = "select fd.FILM_ID,d. DIRECTOR_ID,d.NAME " +
                "from film_directors fd join DIRECTORS d on d.DIRECTOR_ID = fd.DIRECTOR_ID";

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql_film);
        Map<Long, Set<Genre>> setMap = makeGenreMap(genreRows);

        SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sql_director);
        Map<Long, Set<Director>> setMapDirector = makeDirectorMap(directorRows);

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs, setMap, setMapDirector));
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

        String sql_director = "select fd.FILM_ID,d.DIRECTOR_ID,d.NAME " +
                "from film_directors fd join DIRECTORS d on d.DIRECTOR_ID = fd.DIRECTOR_ID " +
                "WHERE fd.FILM_ID = ?";

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql_film, id);
        Map<Long, Set<Genre>> setMap = makeGenreMap(genreRows);

        SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sql_director, id);
        Map<Long, Set<Director>> setMapDirector = makeDirectorMap(directorRows);

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);

        if (filmRows.next()) {
            log.info("Найден фильм: {} {}", filmRows.getString("film_id"), filmRows.getString("NAME"));
            Film film = new Film(filmRows.getLong("film_id"),
                    filmRows.getString("NAME"),
                    filmRows.getString("description"),
                    filmRows.getDate("releaseDate").toLocalDate(),
                    filmRows.getInt("duration"),
                    new MPA(filmRows.getLong("RATING_MPA_ID"), filmRows.getString("MPA_NAME")),
                    setMap.getOrDefault(id, new HashSet<>()),
                    setMapDirector.getOrDefault(id, new HashSet<>())
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

        if (film.getDirectors() != null) {
            String sqlInsertFilmDirector = "INSERT INTO FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) values (?,?)";
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(sqlInsertFilmDirector, id, director.getId());
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

        String sqlDelDirectorFilm = "DELETE FROM FILM_DIRECTORS WHERE FILM_ID = ?";
        jdbcTemplate.update(sqlDelDirectorFilm, film.getId());

        if (film.getGenres() != null) {
            String sqlInsertFilmGenre = "INSERT INTO film_genres (FILM_ID,GENRE_ID) values (?,?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlInsertFilmGenre, film.getId(), genre.getId());
            }
        }

        if (film.getDirectors() != null) {
            String sqlInsertFilmDirector = "INSERT INTO FILM_DIRECTORS (FILM_ID, DIRECTOR_ID) values (?,?)";
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update(sqlInsertFilmDirector, film.getId(), director.getId());
            }
        }

        return getFilm(film.getId());
    }

    private Film makeFilm(ResultSet filmRows, Map<Long, Set<Genre>> setMap, Map<Long, Set<Director>> setMapDirector) throws SQLException {
        Long film_id = filmRows.getLong("film_id");
        return new Film(film_id,
                filmRows.getString("fname"),
                filmRows.getString("description"),
                filmRows.getDate("releaseDate").toLocalDate(),
                filmRows.getInt("duration"),
                new MPA(filmRows.getLong("RATING_MPA_ID"), filmRows.getString("mpa_name")),
                setMap.getOrDefault(film_id, new HashSet<>()),
                setMapDirector.getOrDefault(film_id, new HashSet<>()));
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

    private Map<Long, Set<Director>> makeDirectorMap(SqlRowSet directorRowSet) {
        Map<Long, Set<Director>> directorMap = new HashMap<>();
        while (directorRowSet.next()) {
            Long film_id = directorRowSet.getLong("film_id");
            Long director_id = directorRowSet.getLong("DIRECTOR_ID");
            String director_name = directorRowSet.getString("NAME");

            if (directorMap.containsKey(film_id)) {
                directorMap.get(film_id).add(new Director(director_id, director_name));
            } else {
                directorMap.put(film_id, new HashSet<>());
                directorMap.get(film_id).add(new Director(director_id, director_name));
            }
        }
        return directorMap;
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

        String sql_director = "select fd.FILM_ID,d. DIRECTOR_ID,d.NAME " +
                "from film_directors fd join DIRECTORS d on d.DIRECTOR_ID = fd.DIRECTOR_ID";

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql_film);
        Map<Long, Set<Genre>> setMap = makeGenreMap(genreRows);

        SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sql_director);
        Map<Long, Set<Director>> setMapDirector = makeDirectorMap(directorRows);

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs, setMap, setMapDirector), count);
        return films;
    }

    @Override
    public void removeByFilmId(Long filmId) {
        String sqlString = "delete from FILMS where FILM_ID=?";
        if (jdbcTemplate.update(sqlString, filmId) == 0) {
            throw new FilmNotFoundException();
        }
    }


    @Override
    public List<Film> getDirectorFilms(Long directorId, String sortBy) {
        String sqlGetDirector = "select  d.DIRECTOR_ID, d.NAME " +
                "from directors as d" +
                " where d.DIRECTOR_ID = ?";

        SqlRowSet getDirectorRows = jdbcTemplate.queryForRowSet(sqlGetDirector, directorId);

        if (getDirectorRows.next()) {
            log.info("Найден режиссер с id: {}", getDirectorRows.getString("director_id"));
        } else {
            log.info("Режиссер с идентификатором {} не найден.", directorId);
            throw new DirectorNotFoundException();
        }


        if (sortBy.equals("likes")) {
            String sql = "select  film_id, f.name as fname, description, releaseDate, duration, " +
                    "f.RATING_MPA_ID, rm.name as mpa_name " +
                    "from films as f join rating_mpa as rm on f.RATING_MPA_ID = rm.RATING_MPA_ID " +
                    "ORDER BY f.LIKES_COUNTER DESC";

            String sql_film = "select fg.FILM_ID,g.GENRE_ID,g.NAME " +
                    "from film_genres fg join GENRES g on g.GENRE_ID = fg.GENRE_ID";

            String sql_director = "select fd.FILM_ID,d. DIRECTOR_ID,d.NAME " +
                    "from film_directors fd join DIRECTORS d on d.DIRECTOR_ID = fd.DIRECTOR_ID";

            SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql_film);
            Map<Long, Set<Genre>> setMap = makeGenreMap(genreRows);

            SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sql_director);
            Map<Long, Set<Director>> setMapDirector = makeDirectorMap(directorRows);

            List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs, setMap, setMapDirector));

            List<Film> directorFilms = new ArrayList<>();
            for (Film film : films) {
                for (Director director : film.getDirectors()) {
                    if (director.getId() == directorId) {
                        directorFilms.add(film);
                    }
                }
            }
            return directorFilms;
        } else {
            String sql = "select  film_id, f.name as fname, description, releaseDate, duration, " +
                    "f.RATING_MPA_ID, rm.name as mpa_name " +
                    "from films as f join rating_mpa as rm on f.RATING_MPA_ID = rm.RATING_MPA_ID " +
                    "ORDER BY f.RELEASEDATE ASC";

            String sql_film = "select fg.FILM_ID,g.GENRE_ID,g.NAME " +
                    "from film_genres fg join GENRES g on g.GENRE_ID = fg.GENRE_ID";

            String sql_director = "select fd.FILM_ID,d. DIRECTOR_ID,d.NAME " +
                    "from film_directors fd join DIRECTORS d on d.DIRECTOR_ID = fd.DIRECTOR_ID";

            SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql_film);
            Map<Long, Set<Genre>> setMap = makeGenreMap(genreRows);

            SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sql_director);
            Map<Long, Set<Director>> setMapDirector = makeDirectorMap(directorRows);

            List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs, setMap, setMapDirector));

            List<Film> directorFilms = new ArrayList<>();
            for (Film film : films) {
                for (Director director : film.getDirectors()) {
                    if (director.getId() == directorId) {
                        directorFilms.add(film);
                    }
                }
            }
            return directorFilms;
        }
    }

    @Override
    public List<Film> getSortedByPopularityListOfFilms(Long userId, Long friendId) {

        String sql = "select  film_id, f.name as fname, description, releaseDate, duration, " +
                "f.RATING_MPA_ID, rm.name as mpa_name " +
                "from films as f join rating_mpa as rm on f.RATING_MPA_ID = rm.RATING_MPA_ID " +
                "ORDER BY f.LIKES_COUNTER DESC";

        String sql_film = "select fg.FILM_ID,g.GENRE_ID,g.NAME " +
                "from film_genres fg join GENRES g on g.GENRE_ID = fg.GENRE_ID";

        String sql_director = "select fd.FILM_ID,d. DIRECTOR_ID,d.NAME " +
                "from film_directors fd join DIRECTORS d on d.DIRECTOR_ID = fd.DIRECTOR_ID";

        SqlRowSet genreRows = jdbcTemplate.queryForRowSet(sql_film);
        Map<Long, Set<Genre>> setMap = makeGenreMap(genreRows);

        SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sql_director);
        Map<Long, Set<Director>> setMapDirector = makeDirectorMap(directorRows);

        List<Film> films = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs, setMap, setMapDirector));

        String sqlGetLikeByUserFilms = "select FILM_ID from LIKES where USER_ID = ?";
        List<Integer> userFilms = jdbcTemplate.query(sqlGetLikeByUserFilms, (rs, rowNum) -> {
            Integer id = rs.getInt("FILM_ID");
            if (id != null) {
                return id;
            } else {
                return null;
            }
        }, userId);

        String sqlGetLikeByFriendFilms = "select FILM_ID from LIKES where USER_ID = ?";
        List<Integer> friendFilms = jdbcTemplate.query(sqlGetLikeByFriendFilms, (rs, rowNum) -> {
            Integer id = rs.getInt("FILM_ID");
            if (id != null) {
                return id;
            } else {
                return null;
            }
        }, friendId);

        List<Integer> commonFilmsId = new ArrayList<>();

        for (Integer i : userFilms) {
            for (Integer k : friendFilms) {
                if (i == k) {
                    commonFilmsId.add(i);
                }
            }
        }

        List<Film> commonFilms = new ArrayList<>();

        for (Integer i : commonFilmsId) {
            Film film = getFilm((i.longValue())).orElseThrow(FilmNotFoundException::new);
            commonFilms.add(film);
        }

        List<Film> sortedCommonFilmList = new ArrayList<>();

        for (Film film : films) {
            for (Film commonFilm : commonFilms) {
                if (film.getId() == commonFilm.getId()) {
                    sortedCommonFilmList.add(film);
                }
            }
        }
        return sortedCommonFilmList;
    }
}
