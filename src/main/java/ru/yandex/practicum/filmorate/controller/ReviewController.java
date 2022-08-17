package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.ArrayList;
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
    public List<Review> getAllReview(@RequestParam(name = "filmId", required = false) String filmId, @RequestParam(defaultValue = "10") int count) {
        return new ArrayList<>();
    }

    @PostMapping
    public Optional<Review> add(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Optional<Review> update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping
    public Optional<Review> deleteReview(@PathVariable Long id) {
        log.info("Выполнен запрос удаление отзывы по ID = " + id);
        //Optional<Film> optionalFilm = filmService.getFilm(id);
        //optionalFilm.orElseThrow(() -> new FilmNotFoundException());
        //return optionalFilm;
        return Optional.empty();
    }

    @GetMapping("{id}")
    public Optional<Review> getReview(@PathVariable Long id) {
        log.info("Выполнен запрос получение отзывы по ID = " + id);
        Optional<Review> optionalReview = reviewService.getReview(id);
        optionalReview.orElseThrow(() -> new FilmNotFoundException());
        return optionalReview;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {

        log.info("Выполнен запрос добавления лайка по фильму ID:" + id + " от пользователя с ID:" + userId);
        //filmService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {

        log.info("Выполнен запрос добавления лайка по фильму ID:" + id + " от пользователя с ID:" + userId);
        //filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {

        log.info("Выполнен запрос добавления лайка по фильму ID:" + id + " от пользователя с ID:" + userId);
        //filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {

        log.info("Выполнен запрос добавления лайка по фильму ID:" + id + " от пользователя с ID:" + userId);
        //filmService.addLike(id, userId);
    }
}
