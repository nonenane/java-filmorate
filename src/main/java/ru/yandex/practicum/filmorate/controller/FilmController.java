package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;
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
            throw new ValidationException("Ошибка проверки");
        }

        if (filmService.getFilm(film.getId()) == null) {
            throw new ValidationException("Не найден id фильма");
        }

        Film filmForSave = filmService.update(film);
        log.info("Запись id=" + film.getId() + " по фильму обновлена");
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        if (filmService.getFilm(id) == null)
            throw new FilmNotFoundException();

       // if (userService.getUser(userId) == null)
       //     throw new UserNotFoundException();

        log.info("Выполнен запрос addLike.");
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        if (filmService.getFilm(id) == null)
            throw new FilmNotFoundException();

        //if (userService.getUser(userId) == null)
        //    throw new UserNotFoundException();

        log.info("Выполнен запрос removeLike.");
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getFilmsWithMostLikes(@RequestParam Optional<Integer> count) {
        log.info("Выполнен запрос getFilmsWithMostLikes.");
        return filmService.getFilmsWithMostLikes(count.orElse(10));
    }
}
