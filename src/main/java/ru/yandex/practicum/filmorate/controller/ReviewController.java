package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @GetMapping
    public List<Review> getAllReview(@RequestParam(name = "filmId", required = false) Long filmId, @RequestParam(defaultValue = "10") int count) {

        if (filmId == null) {
            return reviewService.getAllReviews(count);
        } else {
            return reviewService.getAllReviews(filmId, count);
        }
    }

    @PostMapping
    public Optional<Review> add(@Valid @RequestBody Review review) {
        log.info("Выполнен запрос добавления отзыва");
        return reviewService.create(review);
    }

    @PutMapping
    public Optional<Review> update(@Valid @RequestBody Review review) {
        log.info("Выполнен запрос обновления отзывы по ID = " + review.getId());
        return reviewService.update(review);
    }

    @DeleteMapping("{id}")
    public void deleteReview(@PathVariable Long id) {
        log.info("Выполнен запрос удаление отзывы по ID = " + id);
        reviewService.delete(id);
    }

    @GetMapping("{id}")
    public Optional<Review> getReview(@PathVariable Long id) {
        log.info("Выполнен запрос получение отзывы по ID = " + id);
        Optional<Review> optionalReview = reviewService.getReview(id);
        optionalReview.orElseThrow(() -> new ReviewNotFoundException());
        return optionalReview;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Выполнен запрос добавления лайка по отзыву ID:" + id + " от пользователя с ID:" + userId);
        reviewService.addLike(id, userId, true);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Выполнен запрос добавления дизлайка по отзыву ID:" + id + " от пользователя с ID:" + userId);
        reviewService.addLike(id, userId, false);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {

        log.info("Выполнен запрос удаления лайка по отзыву ID:" + id + " от пользователя с ID:" + userId);
        reviewService.removeLike(id, userId, true);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Выполнен запрос удаления дизлайка по отзыву ID:" + id + " от пользователя с ID:" + userId);
        reviewService.removeLike(id, userId, false);
    }
}
