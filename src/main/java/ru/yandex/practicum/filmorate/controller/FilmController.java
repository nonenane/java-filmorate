package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
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
    public Film create(@Valid @RequestBody Film film) {

        if (film.getId() != null) {
            log.info("Запись уже присутствует.");
            throw new ValidationException("Не пустой id");
        }

        film = filmService.create(film);
        log.info("Создана запись по фильму. Кол-во записей:" + filmService.getAllFilms().size());
        return film;
    }

    @PutMapping()
    public Film update(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            throw new ValidationException("Не задан ID фильма");
        }

        Optional<Film> optionalFilm = Optional.ofNullable(filmService.getFilm(film.getId()));
        optionalFilm.orElseThrow(() -> new FilmNotFoundException());

        film = filmService.update(film);
        log.info("Запись id=" + film.getId() + " по фильму обновлена");
        return film;
    }

    @GetMapping("{id}")
    public Film getFilm(@PathVariable Long id) {
        log.info("Выполнен запрос получения фильма по ID = " + id);

        Optional<Film> optionalFilm = Optional.ofNullable(filmService.getFilm(id));
        Film film = optionalFilm.orElseThrow(() -> new FilmNotFoundException());
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {

        log.info("Выполнен запрос добавления лайка по фильму ID:" + id + " от пользователя с ID:" + userId);

        Optional<Film> optionalFilm = Optional.ofNullable(filmService.getFilm(id));
        optionalFilm.orElseThrow(() -> new FilmNotFoundException());

        Optional<User> optionalUser = Optional.ofNullable(userService.getUser(userId));
        optionalUser.orElseThrow(() -> new UserNotFoundException());

        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {

        log.info("Выполнен запрос удаления лайка по фильму ID:" + id + " от пользователя с ID:" + userId);

        Optional<Film> optionalFilm = Optional.ofNullable(filmService.getFilm(id));
        optionalFilm.orElseThrow(() -> new FilmNotFoundException());

        Optional<User> optionalUser = Optional.ofNullable(userService.getUser(userId));
        optionalUser.orElseThrow(() -> new UserNotFoundException());

        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getFilmsWithMostLikes(@RequestParam Optional<Integer> count) {
        log.info("Выполнен запрос получения " + count.orElse(10) + " популярных фильмов.");
        return filmService.getFilmsWithMostLikes(count.orElse(10));
    }
}
