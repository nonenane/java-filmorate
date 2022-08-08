package ru.yandex.practicum.filmorate.storage.friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsStorage {

    boolean addFriend(Long userId, Long friendId);

    List<User> getFriends(Long userId);

    boolean removeFriend(Long userId, Long friendId);

    List<User> getCommonFriends(Long userId, Long otherId);
}
