package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAllFilms();

    Optional<Film> getFilm(Long id);

    Optional<Film> create(Film film);

    Optional<Film> update(Film film);

    List<Film> getPopularFilm(Integer count);

    void removeByFilmId(Long userId);
}
