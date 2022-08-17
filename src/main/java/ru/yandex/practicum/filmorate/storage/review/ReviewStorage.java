package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    List<Review> getAllReviews(int count);

    Optional<Review> getReview(Long id);

    Optional<Review> create(Review review);

    Optional<Review> update(Review review);

    void addLike(boolean isPositive);
    void removeLike(boolean isPositive);
}
