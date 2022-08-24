package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    protected FeedService feedService;

    @Autowired
    public FilmController(FilmService filmService, FeedService feedService) {
        this.filmService = filmService;
        this.feedService = feedService;
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
        log.info("Выполнен запрос removeByFilmId для ID:" + id);
        filmService.removeByFilmId(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {

        log.info("Выполнен запрос добавления лайка по фильму ID:" + id + " от пользователя с ID:" + userId);
        filmService.addLike(id, userId);
        feedService.addFeed(userId, id, "ADD", "LIKE");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Выполнен запрос удаления лайка по фильму ID:" + id + " от пользователя с ID:" + userId);
        filmService.removeLike(id, userId);
        feedService.addFeed(userId, id, "REMOVE", "LIKE");
    }

    /*@GetMapping("/popular")
    public List<Film> getFilmsWithMostLikes(@RequestParam(defaultValue = "10") int count) {
        log.info("Выполнен запрос получения " + count + " популярных фильмов.");
        return filmService.getFilmsWithMostLikes(count);
    }*/

    @GetMapping("/popular")
    public List<Film> getPopularFilmsByGenreAndYear(@RequestParam(defaultValue = "10") int count,
                                                    @RequestParam(required = false) Long genreId,
                                                    @RequestParam(name = "year",required = false) Integer releaseYear) {

        if (genreId == null && releaseYear == null) {
            log.info("Выполнен запрос получения " + count + " популярных фильмов.");
            return filmService.getFilmsWithMostLikes(count);
        } else {
            log.info("Выполнен запрос получения " + count + " популярных фильмов в жанре ID " + genreId
                    + " с годом релиза " + releaseYear);
            return filmService.getPopularFilmsByGenreAndYear(count, genreId, releaseYear);
        }
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

    @GetMapping("/search")
    public List<Film> getFilmsBySearch(@RequestParam String query, @RequestParam String by) {
        log.info("Выполнен поиск");
        return filmService.getFilmsBySearch(query, by);
    }
}
