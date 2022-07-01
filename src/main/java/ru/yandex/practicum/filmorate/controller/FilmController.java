package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends AbstractController<Long, Film> {

    private Long idCounter = 1L;

    public FilmController() {
        super(new HashMap<>());
    }

    @GetMapping()
    public List<Film> findAll() {
        return new ArrayList<>(resourceStorage.values());
    }

    @PostMapping()
    public Film create(@Valid @RequestBody Film film) {
        if (!validateFilm(film)) {
            throw new ValidationException("Ошибка проверки");
        }

        if (film.getId() != null) {
            log.info("Запись уже присутствует.");
            throw new ValidationException("Не пустой id");
        }

        film = new Film(setId(), film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration());


        resourceStorage.put(film.getId(), film);
        log.info("Создана запись по фильму. Кол-во записей:" + resourceStorage.size());
        return film;
    }

    @PutMapping()
    public Film update(@RequestBody Film film) {
        if (!resourceStorage.containsKey(film.getId())) {
            throw new ValidationException("Не найден id фильма");
        }

        if (!validateFilm(film)) {
            throw new ValidationException("Ошибка проверки");
        }

        resourceStorage.put(film.getId(), film);
        log.info("Запись id=" + film.getId() + " по фильму обновлена");
        return film;
    }

    private boolean validateFilm(Film film) {
        if (film.getName().isEmpty()) {
            log.info("Пустое наименование фильма");
            return false;
        }
        if (film.getDescription().trim().length() > 200) {
            log.info("Длина фильма больше 200");
            return false;
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата выпуска фильма раньше 28.12.1895");
            return false;
        }
        if (film.getDuration() < 0) {
            log.info("Продолжительность отрицательная");
            return false;
        }
        return true;
    }

    private Long setId() {
        return idCounter++;
    }
}
