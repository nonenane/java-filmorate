package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    Optional<Director> create(Director director);

    Optional<Director> getDirector(long id);

    List<Director> getAll();

    Optional<Director> update(Director director);

    void removeDirector(Long id);
}
