package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class User {
    private final Long id;
    @Email
    @NonNull
    @NotBlank
    private final String email;
    @NonNull
    @NotBlank
    private final String login;
    private final String name;
    private final LocalDate birthday;
}
