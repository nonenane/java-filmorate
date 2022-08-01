package ru.yandex.practicum.filmorate.storage.users;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    List<User> getAllUsers();

    Optional<User> getUser(Long id);

    Optional<User> create(User user);

    Optional<User> update(User user);
}
