package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.util.EmailValidator;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;


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
        log.info("Выполнен запрос removeByUserId для ID:" + id );
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
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {

        log.info("Выполнен запрос removeFriend для ID:" + id + " добавление ID:" + friendId);

        Optional<User> optionalUser = userService.getUser(id);
        optionalUser.orElseThrow(() -> new UserNotFoundException());

        optionalUser = userService.getUser(friendId);
        optionalUser.orElseThrow(() -> new UserNotFoundException());

        userService.removeFriend(id, friendId);
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

        if (userService.getUser(id) == null || userService.getUser(otherId) == null)
            throw new UserNotFoundException();


        return userService.getCommonFriends(id, otherId);
    }
}
