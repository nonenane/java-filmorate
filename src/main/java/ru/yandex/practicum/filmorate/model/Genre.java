package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Genre {

    @NonNull
    @Size(min = 1, max = 6)
    private Long id;
    @NotBlank
    @NonNull
    private String name;

    @JsonCreator
    public Genre(Long id) {
        this.id = id;
    }
}
