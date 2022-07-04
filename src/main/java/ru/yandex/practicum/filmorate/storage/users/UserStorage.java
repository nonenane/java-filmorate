package ru.yandex.practicum.filmorate.storage.users;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getList();

    User getUser(Long id);

    User create(User user);

    User update(User user);
}
