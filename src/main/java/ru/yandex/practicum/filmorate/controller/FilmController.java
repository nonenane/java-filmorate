package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
@RestController
public class FilmController extends AbstractController<Long, Film> {

    private Long idCounter = 1L;

    public FilmController() {
        super(new HashMap<>());
    }

    @GetMapping("/films")
    public List<Film> findAll() {
        return new ArrayList<>(resourceStorage.values());
    }

    @PostMapping("/films")
    public Film create(@RequestBody Film film) {
        if (!validateFilm(film)) {
            throw new ValidationException("Error Validate");
        }

        if (film.getId() == null) {
            film = setId(film);
        }

        resourceStorage.put(film.getId(), film);

        return film;
    }

    @PutMapping("/films")
    public Film update(@RequestBody Film film) {
        if (!resourceStorage.containsKey(film.getId())) {
            throw new ValidationException("not search key");
        }

        if (!validateFilm(film)) {
            throw new ValidationException("Error Validate");
        }

        resourceStorage.put(film.getId(), film);

        return film;
    }

    private boolean validateFilm(Film film) {
        if (film.getName().isEmpty()) {
            log.info("Empty film name");
            return false;
        }
        if (film.getDescription().trim().length() > 200) {
            log.info("Description length>200");
            return false;
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("ReleaseDate");
            return false;
        }
        if (film.getDuration() < 0) {
            log.info("Duration");
            return false;
        }
        return true;
    }

    private Film setId(Film film) {
        return new Film(idCounter++, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());
    }
}
