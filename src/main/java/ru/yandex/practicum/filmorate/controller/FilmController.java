package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping()
    public List<Film> findAll() {
        log.info("Получение всех фильмов");
        return filmService.getAllFilms();
    }

    @PostMapping()
    public Optional<Film> create(@Valid @RequestBody Film film) {

        if (film.getId() != null) {
            log.info("Запись уже присутствует.");
            throw new ValidationException("Не пустой id");
        }

        log.info("Создана запись по фильму. Кол-во записей:" + filmService.getAllFilms().size());
        return filmService.create(film);
    }

    @PutMapping()
    public Optional<Film> update(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Не задан ID фильма");
        }

        Optional<Film> optionalFilm = filmService.update(film);
        optionalFilm.orElseThrow(() -> new FilmNotFoundException());
        log.info("Запись id=" + film.getId() + " по фильму обновлена");
        return optionalFilm;
    }

    @GetMapping("{id}")
    public Optional<Film> getFilm(@PathVariable Long id) {
        log.info("Выполнен запрос получения фильма по ID = " + id);
        Optional<Film> optionalFilm = filmService.getFilm(id);
        optionalFilm.orElseThrow(() -> new FilmNotFoundException());
        return optionalFilm;
    }
    @DeleteMapping(value = "/{id}")
    public void removeByFilmId(@Valid @PathVariable Long id) {
        log.info("Выполнен запрос removeByFilmId для ID:" + id );
        filmService.removeByFilmId(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {

        log.info("Выполнен запрос добавления лайка по фильму ID:" + id + " от пользователя с ID:" + userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Выполнен запрос удаления лайка по фильму ID:" + id + " от пользователя с ID:" + userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getFilmsWithMostLikes(@RequestParam(defaultValue = "10") int count) {
        log.info("Выполнен запрос получения " + count + " популярных фильмов.");
        return filmService.getFilmsWithMostLikes(count);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getDirectorFilms(@RequestParam String sortBy, @PathVariable Long directorId) {
        log.info("Выполнен запрос получения популярных фильмов режиссера с id " + directorId);
        return filmService.getDirectorFilms(directorId, sortBy);
    }

    @GetMapping("/common")
    public List<Film> getSortedByPopularityListOfFilms(@RequestParam Long userId, @RequestParam Long friendId) {
        log.info("Выполнен запрос получения общих популярных фильмов пользователя с id " + userId
                + " и его друга с id " + friendId);
        return filmService.getSortedByPopularityListOfFilms(userId, friendId);
    }
}
