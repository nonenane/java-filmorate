package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
public class Film {
    private final Long id;
    @NonNull
    @NotBlank
    private final String name;
    @Max(200)
    private final String description;
    private final LocalDate releaseDate;
    private final Integer duration;
}
