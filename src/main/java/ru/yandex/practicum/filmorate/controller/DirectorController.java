package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping()
    public List<Director> getDirectors() {
        log.info("Выполнен запрос получения списка всех режиссеров");
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Optional<Director> getDirectorById(@PathVariable Long id) {
        log.info("Выполнен запрос получения режиссера по ID = " + id);
        Optional<Director> optionalDirector = directorService.getDirector(id);
        optionalDirector.orElseThrow(() -> new DirectorNotFoundException());
        return optionalDirector;
    }

    @PostMapping()
    public Optional<Director> create(@Valid @RequestBody Director director) {

        log.info("Создана запись режиссера");
        return directorService.create(director);
    }

    @PutMapping()
    public Optional<Director> update(@Valid @RequestBody Director director) {
        if (director.getId() == null) {
            throw new ValidationException("Не задан ID режиссера");
        }

        Optional<Director> optionalDirector = directorService.update(director);
        optionalDirector.orElseThrow(() -> new DirectorNotFoundException());
        log.info("Запись id=" + director.getId() + " по режиссера обновлена");
        return optionalDirector;
    }

    @DeleteMapping("/{id}")
    public void removeDirector(@PathVariable Long id) {
        log.info("Выполнен запрос удаления режиссера с ID:" + id);
        directorService.removeDirector(id);
    }
}
