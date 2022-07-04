package ru.yandex.practicum.filmorate.storage.users;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    protected Long currentID = 0L;
    protected Map<Long, User> userMap;

    public InMemoryUserStorage() {
        userMap = new HashMap<>();
    }

    @Override
    public List<User> getList() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public User getUser(Long id) {
        return userMap.get(id);
    }

    @Override
    public User create(User user) {
        return new User(setId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
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
