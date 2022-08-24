package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class ReviewService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final ReviewStorage reviewStorage;

    public ReviewService(FilmStorage filmStorage, UserStorage userStorage, ReviewStorage reviewStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.reviewStorage = reviewStorage;
    }

    public Optional<Review> create(Review review) {

        filmStorage.getFilm(review.getFilmId()).orElseThrow(() -> new FilmNotFoundException());
        userStorage.getUser(review.getUserId()).orElseThrow(() -> new UserNotFoundException());

        return reviewStorage.create(review);
    }

    public Optional<Review> update(Review review) {

        filmStorage.getFilm(review.getFilmId()).orElseThrow(() -> new FilmNotFoundException());
        userStorage.getUser(review.getUserId()).orElseThrow(() -> new UserNotFoundException());

        return reviewStorage.update(review);
    }

    public void delete(Long id) {
        reviewStorage.delete(id);
    }

    public Optional<Review> getReview(Long id) {
        return reviewStorage.getReview(id);
    }

    public List<Review> getAllReviews(Long filmId, int count) {

        if (filmId == null) {
            return reviewStorage.getAllReviews(count);
        } else {
            filmStorage.getFilm(filmId).orElseThrow(() -> new FilmNotFoundException());
            return reviewStorage.getAllReviews(filmId, count);
        }
    }

    public void addLike(Long reviewID, Long userId, boolean isPositive) {
        reviewStorage.addLike(reviewID, userId, isPositive);
    }

    public void removeLike(Long reviewID, Long userId, boolean isPositive) {
        reviewStorage.removeLike(reviewID, userId, isPositive);
    }
}
