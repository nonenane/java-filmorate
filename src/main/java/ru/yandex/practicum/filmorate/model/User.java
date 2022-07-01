package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;
import org.springframework.cglib.core.Local;

import javax.validation.constraints.*;
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
    @Pattern(regexp = "^[a-zA-Z0-9._-]{3,}$")
    private final String login;
    private final String name;
    @PastOrPresent
    private final LocalDate birthday;
}
