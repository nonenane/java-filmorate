package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;

@Data
public class Review {
    @JsonProperty("reviewId")
    private final Long id;
    @NonNull
    @NotBlank
    private final String content;
    @NonNull
    private final Boolean isPositive;
    @NonNull
    private final Long userId;
    @NonNull
    private final Long filmId;
    private final int useful;
}
