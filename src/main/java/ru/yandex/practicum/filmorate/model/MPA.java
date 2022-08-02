package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class MPA {
    @NotNull
    @Size(min = 1, max = 5)
    private Long id;

    private String name;

    @JsonCreator
    public MPA(Long id) {
        this.id = id;
    }
}
