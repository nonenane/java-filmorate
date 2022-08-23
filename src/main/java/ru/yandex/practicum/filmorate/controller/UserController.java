package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    protected UserService userService;
    protected FeedService feedService;


    @Autowired
    public UserController(UserService userService, FeedService feedService) {
        this.userService = userService;
        this.feedService = feedService;
    }

    @GetMapping()
    public List<User> findAll() {
        return userService.getAllUsers();
    }

    @PostMapping()
    public Optional<User> create(@Valid @RequestBody User user) {

        if (user.getId() != null) {
            log.info("Запись уже присутствует.");
            throw new ValidationException("Не пустой id");
        }

        Optional<User> optionalUser = userService.create(user);
        log.info("Создана запись по пользователю. Кол-во записей:" + userService.getAllUsers().size());

        return optionalUser;
    }

    @PutMapping()
    public Optional<User> update(@Valid @RequestBody User user) {
        Optional<User> optionalUser = userService.update(user);
        optionalUser.orElseThrow(() -> new UserNotFoundException());


        log.info("Запись id=" + user.getId() + " по пользователю обновлена");
        return optionalUser;
    }

    @GetMapping("/{id}")
    public Optional<User> getUser(@PathVariable Long id) {

        log.info("Выполнен запрос getUser по ID:" + id);

        Optional<User> optionalUser = userService.getUser(id);
        optionalUser.orElseThrow(() -> new UserNotFoundException());

        return optionalUser;
    }

    @DeleteMapping(value = "/{id}")
    public void removeByUserId(@Valid @PathVariable Long id) {
        log.info("Выполнен запрос removeByUserId для ID:" + id);
        userService.removeByUserId(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {

        log.info("Выполнен запрос addFriend для ID:" + id + " добавление ID:" + friendId);

        Optional<User> optionalUser = userService.getUser(id);
        optionalUser.orElseThrow(() -> new UserNotFoundException());

        optionalUser = userService.getUser(friendId);
        optionalUser.orElseThrow(() -> new UserNotFoundException());

        userService.addFriend(id, friendId);
        feedService.addFeed(id,friendId,"ADD","FRIEND");
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {

        log.info("Выполнен запрос removeFriend для ID:" + id + " добавление ID:" + friendId);

        Optional<User> optionalUser = userService.getUser(id);
        optionalUser.orElseThrow(() -> new UserNotFoundException());

        optionalUser = userService.getUser(friendId);
        optionalUser.orElseThrow(() -> new UserNotFoundException());

        userService.removeFriend(id, friendId);
        feedService.addFeed(id,friendId,"REMOVE","FRIEND");
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {

        log.info("Выполнен запрос getFriends по ID:" + id);
        Optional<User> optionalUser = userService.getUser(id);
        optionalUser.orElseThrow(() -> new UserNotFoundException());

        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {

        log.info("Выполнен запрос getCommonFriends по ID:" + id + " для ID:" + otherId);

        if (userService.getUser(id).isEmpty() || userService.getUser(otherId).isEmpty())
            throw new UserNotFoundException();


        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/feed")
    public List<Feed> getFeeds(@PathVariable Long id) {

        log.info("Выполнен запрос getFeeds по ID:" + id);

        if (userService.getUser(id).isEmpty())
            throw new UserNotFoundException();

        return  feedService.getFeeds(id);
    }

    @GetMapping("/{id}/recommendations")
    public Set<Film> getRecommendations(@PathVariable Long id) {
        log.info("Выполнен запрос getRecommendations по ID:" + id);
        return userService.getRecommendations(id);
    }
}
