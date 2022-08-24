package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.director.impl.DirectorDBStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DirectorService {

    private final DirectorStorage directorStorage;

    public DirectorService(DirectorDBStorage directorStorage) {
        this.directorStorage = directorStorage;
    }


    public List<Director> getDirectors() {
        return directorStorage.getAll();
    }

    public Optional<Director> getDirector(Long id) {
        return directorStorage.getDirector(id);
    }

    public Optional<Director> create(Director director) {
        return directorStorage.create(director);
    }

    public Optional<Director> update(Director director) {
        return directorStorage.update(director);
    }

    public void removeDirector(Long id) {
        directorStorage.removeDirector(id);
    }
}
