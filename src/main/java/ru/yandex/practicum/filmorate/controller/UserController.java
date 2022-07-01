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
@RequestMapping("/users")
public class UserController extends AbstractController<Long, User> {

    private EmailValidator emailValidator = new EmailValidator();
    private Long idCounter = 1L;

    public UserController() {
        super(new HashMap<>());
    }

    @GetMapping()
    public List<User> findAll() {
        return new ArrayList<>(resourceStorage.values());
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) {
        if (!validate(user)) {
            throw new ValidationException("Ошибка проверки");
        }

        if (user.getId() != null) {
            log.info("Запись уже присутствует.");
            throw new ValidationException("Не пустой id");
        }

        user = setNameIfNameIsBlank(new User(setId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday()));

        resourceStorage.put(user.getId(), user);
        log.info("Создана запись по пользователю. Кол-во записей:" + resourceStorage.size());

        return user;
    }

    @PutMapping()
    public User update(@RequestBody User user) {
        if (!resourceStorage.containsKey(user.getId())) {
            throw new ValidationException("Не найден id пользователю");
        }

        if (!validate(user)) {
            throw new ValidationException("Ошибка проверки");
        }

        resourceStorage.put(user.getId(), setNameIfNameIsBlank(user));
        log.info("Запись id=" + user.getId() + " по пользователю обновлена");
        return user;
    }

    private boolean validate(User user) {
        if (user.getLogin().isBlank()) {
            log.info("Пустой логин");
            return false;
        }
        if (user.getEmail().isBlank() || !emailValidator.validateEmail(user.getEmail())) {
            log.info("Не корректная почта");
            return false;
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("День рождение не может быть будущим");
            return false;
        }
        return true;
    }

    private Long setId() {
        return idCounter++;
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
