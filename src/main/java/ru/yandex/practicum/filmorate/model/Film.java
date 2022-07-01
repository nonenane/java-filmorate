package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;
import ru.yandex.practicum.filmorate.myAnnotation.myDate;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class Film {
    private final Long id;
    @NonNull
    @NotBlank
    private final String name;
    @Size(max=200)
    @NonNull
    @NotBlank
    private final String description;
    @NonNull
    @myDate
    private final LocalDate releaseDate;
    @Min(0)
    private final Integer duration;
}
