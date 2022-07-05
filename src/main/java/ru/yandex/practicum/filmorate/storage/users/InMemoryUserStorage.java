package ru.yandex.practicum.filmorate.storage.users;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    protected Long currentID = 1L;
    protected Map<Long, User> userMap;

    public InMemoryUserStorage() {
        userMap = new HashMap<>();
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User getUser(Long id) {
        return userMap.get(id);
    }

    @Override
    public User create(User user) {
        user = new User(setId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        userMap.put(user.getId(), user);
        return user;

    }

    @Override
    public User update(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    private Long setId() {
        return currentID++;
    }
}
