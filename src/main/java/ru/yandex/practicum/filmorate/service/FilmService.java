package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;
    private final LikeStorage likeStorage;

    public FilmService(FilmStorage storage, LikeStorage likeStorage) {
        this.storage = storage;
        this.likeStorage = likeStorage;
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

    public void removeByFilmId(Long userId) throws ValidationException {
        storage.removeByFilmId(userId);
    }

    public boolean addLike(Long filmId, Long userId) {
        likeStorage.addLike(filmId, userId);
        return true;
    }

    public boolean removeLike(Long filmId, Long userId) {
        likeStorage.removeLike(filmId, userId);
        return true;

    }

    public List<Film> getDirectorFilms(Long directorId, String sortBy) {
        return storage.getDirectorFilms(directorId, sortBy);
    }

    public List<Film> getSortedByPopularityListOfFilms(Long userId, Long friendId) {
        return storage.getSortedByPopularityListOfFilms(userId, friendId);
    }

    public List<Film> getFilmsBySearch(String searchQuery, String searchBy) {
        return storage.getFilmsBySearch(searchQuery, searchBy);
    }

    public List<Film> getPopularFilmsByGenreAndYear(Integer count, Long genreId, Integer releaseYear) {
        if (genreId == null && releaseYear == null) {
            return storage.getPopularFilm(count);
        } else {
            return storage.getPopularFilmsByGenreAndYear(count, genreId, releaseYear);
        }
    }
}
