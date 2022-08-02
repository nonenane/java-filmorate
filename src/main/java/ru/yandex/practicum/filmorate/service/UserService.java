package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    protected UserStorage userStorage;
    protected FriendsStorage friendsStorage;

    public UserService(UserStorage userStorage,FriendsStorage friendsStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public Optional<User> getUser(Long id) {
        return userStorage.getUser(id);
    }

    public Optional<User> create(User user) {
        return userStorage.create(setNameIfNameIsBlank(user));
    }

    public Optional<User> update(User user) {
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
        friendsStorage.addFriend(id,idFriend);
    }

    public void removeFriend(Long id, Long idFriend) {
        friendsStorage.removeFriend(id,idFriend);
    }


    public List<User> getAllFriends(Long userId) {
        return friendsStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        return friendsStorage.getCommonFriends(userId,otherId);
    }
}
