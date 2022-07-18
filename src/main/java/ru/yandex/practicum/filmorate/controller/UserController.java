package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.AlreadyFriendsException;
import ru.yandex.practicum.filmorate.exceptions.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.util.EmailValidator;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    protected UserService userService;
    private EmailValidator emailValidator = new EmailValidator();
    private Long idCounter = 1L;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<User> findAll() {
        return userService.getAllUsers();
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) {

        if (user.getId() != null) {
            log.info("Запись уже присутствует.");
            throw new ValidationException("Не пустой id");
        }

        user = userService.create(user);
        log.info("Создана запись по пользователю. Кол-во записей:" + userService.getAllUsers().size());

        return user;
    }

    @PutMapping()
    public User update(@Valid @RequestBody User user) {
        Optional<User> optionalUser = Optional.ofNullable(userService.getUser(user.getId()));
        optionalUser.orElseThrow(() -> new UserNotFoundException());

        user = userService.update(user);
        log.info("Запись id=" + user.getId() + " по пользователю обновлена");
        return user;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {

        log.info("Выполнен запрос getUser по ID:" + id);

        Optional<User> optionalUser = Optional.ofNullable(userService.getUser(id));
        User user = optionalUser.orElseThrow(() -> new UserNotFoundException());

        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {

        log.info("Выполнен запрос addFriend для ID:" + id + " добавление ID:" + friendId);

        Optional<User> optionalUser = Optional.ofNullable(userService.getUser(id));
        optionalUser.orElseThrow(() -> new UserNotFoundException());

        optionalUser = Optional.ofNullable(userService.getUser(friendId));
        optionalUser.orElseThrow(() -> new UserNotFoundException());


        if (userService.getAllFriends(id).contains(friendId))
            throw new AlreadyFriendsException();

        userService.addFriend(id, friendId);

    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {

        log.info("Выполнен запрос removeFriend для ID:" + id + " добавление ID:" + friendId);

        Optional<User> optionalUser = Optional.ofNullable(userService.getUser(id));
        optionalUser.orElseThrow(() -> new UserNotFoundException());

        optionalUser = Optional.ofNullable(userService.getUser(friendId));
        optionalUser.orElseThrow(() -> new UserNotFoundException());

        if (!userService.getAllFriends(id).contains(friendId))
            throw new FriendNotFoundException();

        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {

        log.info("Выполнен запрос getFriends по ID:" + id);
        Optional<User> optionalUser = Optional.ofNullable(userService.getUser(id));
        optionalUser.orElseThrow(() -> new UserNotFoundException());


        return userService.getAllFriends(id).stream()
                .map(userService::getUser)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {

        log.info("Выполнен запрос getCommonFriends по ID:" + id + " для ID:" + otherId);

        if (userService.getUser(id) == null || userService.getUser(otherId) == null)
            throw new UserNotFoundException();


        return userService.getCommonFriends(id, otherId);
    }
}
