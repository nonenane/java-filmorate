package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping()
    public List<Genre> findAll() {
        log.info("Получение всех Жанров");
        return genreService.getAll();
    }

    @GetMapping("{id}")
    public Optional<Genre> getMpa(@PathVariable Long id) {
        log.info("Выполнен запрос получения рейтинга по ID = " + id);
        Optional<Genre> optionalGenre = genreService.getGenre(id);
        optionalGenre.orElseThrow(() -> new FilmNotFoundException());
        return optionalGenre;
    }

}
