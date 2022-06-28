package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractController<K, V> {

    Map<K, V> resourceStorage;

    public AbstractController(Map map) {
        resourceStorage = map;
    }

    @GetMapping()
    public List<V> findAll() {
        return new ArrayList<>(resourceStorage.values());
    }

    @PostMapping()
    public abstract V create(@Valid @RequestBody V v);

    @PutMapping()
    public abstract V update(@Valid @RequestBody V v);
}
