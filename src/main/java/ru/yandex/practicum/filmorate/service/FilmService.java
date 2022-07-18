package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage storage;
    private final Map<Long, Set<Long>> likesMap;

    public FilmService(FilmStorage storage) {
        this.storage = storage;
        this.likesMap = new HashMap<>();;
    }

    public List<Film> getAllFilms() {
        return storage.getAllFilms();
    }

    public Film getFilm(Long id) {
        return storage.getFilm(id);
    }

    public Film create(Film film) {
        return storage.create(film);
    }

    public Film update(Film film) {
        return storage.update(film);
    }

    public boolean addLike(Long filmId, Long userId) {
        initiateCheck(filmId);

        likesMap.get(filmId).add(userId);
        return true;
    }

    public boolean removeLike(Long filmId, Long userId) {
        initiateCheck(filmId);

        likesMap.get(filmId).remove(userId);
        return true;

    }

    public List<Film> getFilmsWithMostLikes(Integer num) {
        return storage.getAllFilms().stream()
                .peek((x) -> initiateCheck(x.getId()))
                .sorted((x, y) -> likesMap.get(y.getId()).size() - likesMap.get(x.getId()).size())
                .limit(num)
                .collect(Collectors.toList());
    }

    private void initiateCheck(Long id) {
        if (!likesMap.containsKey(id))
            likesMap.put(id, new HashSet<>());
    }
}
