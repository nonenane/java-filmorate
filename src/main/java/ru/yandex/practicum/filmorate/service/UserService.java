package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    protected UserStorage userStorage;
    protected FriendsStorage friendsStorage;

    protected FilmStorage filmStorage;
    public UserService(UserStorage userStorage, FriendsStorage friendsStorage, FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.friendsStorage = friendsStorage;
        this.filmStorage = filmStorage;
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

    public void removeByUserId (Long userId) throws ValidationException{
        userStorage.removeByUserId(userId);

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

    public Set<Film> getRecommendations( Long id) {
        return filmStorage.getRecommendation(id);
    }
}
