package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;
    private final Map<Long, Set<Long>> likesMap;

    public FilmService(FilmStorage storage) {
        this.storage = storage;
        this.likesMap = new HashMap<>();
        ;
    }

    public List<Film> getAllFilms() {
        return storage.getAllFilms();
    }

    public Optional<Film> getFilm(Long id) {
        return storage.getFilm(id);
    }

    public Optional<Film> create(Film film) {
        return storage.create(film);
    }

    public Optional<Film> update(Film film) {
        return storage.update(film);
    }

    public boolean addLike(Long filmId, Long userId) {
        storage.addLike(filmId, userId);
        return true;
    }

    public boolean removeLike(Long filmId, Long userId) {
        storage.removeLike(filmId, userId);
        return true;

    }

    public List<Film> getFilmsWithMostLikes(Integer num) {
        return storage.getPopularFilm(num);
    }
}
