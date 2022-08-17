package ru.yandex.practicum.filmorate.storage.review.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Component
@Primary
public class ReviewDbStorage implements ReviewStorage {


    private final JdbcTemplate jdbcTemplate;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Review> getAllReviews(int count) {
        return null;
    }

    @Override
    public Optional<Review> getReview(Long id) {
        String sql = "select  r.REVIEWID, r.CONTENT,r.ISPOSITIVE,r.USER_ID,r.FILM_ID,r.USEFUL " +
                "from reviews as r " +
                " where r.reviewId = ?";

        SqlRowSet reviewRow = jdbcTemplate.queryForRowSet(sql, id);

        if (reviewRow.next()) {
            log.info("Найден фильм: {} {}", reviewRow.getString("reviewId"), reviewRow.getString("content"));
            Review review = new Review(reviewRow.getLong("reviewId"),
                    reviewRow.getString("content"),
                    reviewRow.getBoolean("isPositive"),
                    reviewRow.getLong("user_id"),
                    reviewRow.getLong("film_id"),
                    reviewRow.getInt("useful")
            );
            return Optional.of(review);
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Review> create(Review review) {

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("reviewId");

        Long id = simpleJdbcInsert.executeAndReturnKey(toMapFilm(review)).longValue();

        return getReview(id);
    }

    private Map<String, Object> toMapFilm(Review review) {
        Map<String, Object> values = new HashMap<>();
        values.put("CONTENT", review.getContent());
        values.put("ISPOSITIVE", review.getIsPositive());
        values.put("user_id", review.getUserId());
        values.put("film_id", review.getFilmId());
        values.put("useful", review.getUseful());
        return values;
    }

    @Override
    public Optional<Review> update(Review review) {
        String sql = "update REVIEWS set CONTENT  = ?, ISPOSITIVE = ?, USER_ID = ?, FILM_ID = ?,USEFUL = ? where REVIEWID = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful(),
                review.getId());

        return getReview(review.getId());
    }

    @Override
    public void addLike(boolean isPositive) {

    }

    @Override
    public void removeLike(boolean isPositive) {

    }

}
