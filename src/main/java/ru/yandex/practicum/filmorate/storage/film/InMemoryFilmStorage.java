package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    protected Long currentID = 1L;
    protected Map<Long, Film> filmMap;

    public InMemoryFilmStorage() {
        filmMap = new HashMap<>();
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmMap.values());
    }

    @Override
    public Film getFilm(Long id) {
        return filmMap.get(id);
    }

    @Override
    public Film create(Film film) {
        film = new Film(setId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        filmMap.put(film.getId(), film);
        return film;
    }

    private Long setId() {
        return currentID++;
    }
}
