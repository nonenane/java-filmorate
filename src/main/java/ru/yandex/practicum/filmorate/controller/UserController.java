package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.EmailValidator;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
@RestController
public class UserController extends AbstractController<Long, User> {

    private EmailValidator emailValidator = new EmailValidator();
    private Long idCounter = 1L;

    public UserController() {
        super(new HashMap<>());
    }

    @GetMapping("/users")
    public List<User> findAll() {
        return new ArrayList<>(resourceStorage.values());
    }

    @PostMapping("/users")
    public User create(@Valid @RequestBody User user) {
        if (!validate(user)) {
            throw new ValidationException("Error Validate");
        }

        if (user.getId() == null) {
            user = setNameIfNameIsBlank(setId(user));
        } else {
            user = setNameIfNameIsBlank(user);
        }

        resourceStorage.put(user.getId(), user);

        return user;
    }

    @PutMapping("/users")
    public User update(@RequestBody User user) {
        if (!resourceStorage.containsKey(user.getId())) {
            throw new ValidationException("not search key");
        }

        if (!validate(user)) {
            throw new ValidationException("Error Validate");
        }

        resourceStorage.put(user.getId(), setNameIfNameIsBlank(user));

        return user;
    }

    private boolean validate(User user) {
        if (user.getLogin().isEmpty()) {
            log.info("Empty Login");
            return false;
        }
        if (user.getEmail().isBlank() || !emailValidator.validateEmail(user.getEmail())) {
            log.info("Email");
            return false;
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Birthday");
            return false;
        }
        return true;
    }

    private User setId(User user) {
        return new User(idCounter++, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
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
}
