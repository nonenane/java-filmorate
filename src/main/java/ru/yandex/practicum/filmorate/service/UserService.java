package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    protected UserStorage userStorage;
    protected final Map<Long, Set<Long>> friendMap;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
        this.friendMap = new HashMap<>();
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User getUser(Long id) {
        return userStorage.getUser(id);
    }

    public User create(User user) {
        return userStorage.create(setNameIfNameIsBlank(user));
    }

    public User update(User user) {
        return userStorage.update(setNameIfNameIsBlank(user));
    }

    private User setNameIfNameIsBlank(User user) {
        if (user.getName().isBlank()) {
            return new User(user.getId(),
                    user.getEmail(),
                    user.getLogin(),
                    user.getLogin(),
                    user.getBirthday());
        }
        return user;
    }

    public void addFriend(Long id, Long idFriend) {
        initiateMap(id);
        initiateMap(idFriend);
        friendMap.get(id).add(idFriend);
        friendMap.get(idFriend).add(id);
    }

    public void removeFriend(Long id, Long idFriend) {
        initiateMap(id);
        initiateMap(idFriend);
        friendMap.get(id).remove(idFriend);
        friendMap.get(idFriend).remove(id);
    }

    private void initiateMap(Long id) {
        if (!friendMap.containsKey(id))
            friendMap.put(id, new HashSet<>());
    }

    public List<Long> getAllFriends(Long userId) {
        initiateMap(userId);
        return new ArrayList<>(friendMap.get(userId));
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        initiateMap(userId);
        initiateMap(otherId);
        return friendMap.get(userId).stream()
                .filter((x) -> friendMap.get(otherId).contains(x))
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }
}
