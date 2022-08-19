package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.myAnnotation.myDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
public class Film {
    private final Long id;
    @NonNull
    @NotBlank
    private final String name;
    @Size(max = 200)
    @NonNull
    @NotBlank
    private final String description;
    @NonNull
    //@myDate
    private final LocalDate releaseDate;
    @Min(0)
    private final Integer duration;
    @NonNull
    private final MPA mpa;
    private final Set<Genre> genres;
    private final Set<Director> directors;
}
