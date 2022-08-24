package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Director {

    @NonNull
    private Long id;
    @NotBlank
    @NonNull
    private String name;

    @JsonCreator
    public Director(Long id) {
        this.id = id;
    }
}
