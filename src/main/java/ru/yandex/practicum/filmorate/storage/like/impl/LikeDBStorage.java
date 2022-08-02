package ru.yandex.practicum.filmorate.storage.like.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

@Slf4j
@Component
@Primary
public class LikeDBStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    public LikeDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addLike(Long idFilm, Long idUser) {

        findFilm(idFilm);
        findUser(idUser);

        String sqlAddLike = "MERGE INTO likes (FILM_ID, USER_ID) values (?,?)";
        jdbcTemplate.update(sqlAddLike, idFilm, idUser);

        String sqlUpdateCountLike = "UPDATE FILMS f SET f.LIKES_COUNTER = (select count(l.USER_ID) from LIKES l where l.FILM_ID = ?) WHERE f.FILM_ID = ?";
        jdbcTemplate.update(sqlUpdateCountLike, idFilm, idFilm);
    }

    @Override
    public void removeLike(Long idFilm, Long idUser) {

        findFilm(idFilm);
        findUser(idUser);

        String sqlAddLike = "DELETE FROM likes WHERE FILM_ID = ? and  USER_ID = ?";
        jdbcTemplate.update(sqlAddLike, idFilm, idUser);

        String sqlUpdateCountLike = "UPDATE FILMS f SET f.LIKES_COUNTER = (select count(l.USER_ID) from LIKES l where l.FILM_ID = ?) WHERE f.FILM_ID = ?";
        jdbcTemplate.update(sqlUpdateCountLike, idFilm, idFilm);
    }

    private void findUser(Long idUser)
    {
        String sql = "select * from USERS where USER_ID = ?";
        if(!jdbcTemplate.queryForRowSet(sql,idUser).next())
        {
             throw  new UserNotFoundException();
        }
    }

    private void findFilm(Long idFilm)
    {
        String sql = "select * from FILMS where FILM_ID = ?";
        if(!jdbcTemplate.queryForRowSet(sql,idFilm).next())
        {
            throw  new FilmNotFoundException();
        }
    }
}
