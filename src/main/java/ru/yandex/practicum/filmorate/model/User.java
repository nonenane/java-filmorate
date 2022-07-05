package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

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
