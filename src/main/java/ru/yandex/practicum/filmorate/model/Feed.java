package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.PastOrPresent;
import java.time.Instant;

@Data
public class Feed {

    @JsonProperty("eventId")
    private final Long id;

    @NonNull
    @PastOrPresent
    private final Long timestamp;
    @NonNull
    private final Long userId;
    @NonNull
    private final Long entityId;
    @NonNull
    private final String eventType;
    @NonNull
    private final String operation;
}
