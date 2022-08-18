package ru.yandex.practicum.filmorate.storage.review.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
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

        String sql = "select  r.REVIEWID, r.CONTENT,r.ISPOSITIVE,r.USER_ID,r.FILM_ID,r.USEFUL " +
                "from reviews as r " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), count);
    }

    @Override
    public List<Review> getAllReviews(Long filmId, int count) {
        String sql = "select  r.REVIEWID, r.CONTENT,r.ISPOSITIVE,r.USER_ID,r.FILM_ID,r.USEFUL " +
                "from reviews as r " +
                "WHERE FILM_ID = ? " +
                "ORDER BY USEFUL DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), filmId, count);
    }

    private Review makeReview(ResultSet reviewRow) throws SQLException {
        return new Review(reviewRow.getLong("REVIEWID"),
                reviewRow.getString("CONTENT"),
                reviewRow.getBoolean("ISPOSITIVE"),
                reviewRow.getLong("USER_ID"),
                reviewRow.getLong("FILM_ID"),
                reviewRow.getInt("USEFUL")
        );
    }

    @Override
    public Optional<Review> getReview(Long id) {
        String sql = "select  r.REVIEWID, r.CONTENT,r.ISPOSITIVE,r.USER_ID,r.FILM_ID,r.USEFUL " +
                "from reviews as r " +
                " where r.reviewId = ?";

        SqlRowSet reviewRow = jdbcTemplate.queryForRowSet(sql, id);

        if (reviewRow.next()) {
            log.info("Отзыв : {} {}", reviewRow.getString("reviewId"), reviewRow.getString("content"));
            Review review = new Review(reviewRow.getLong("reviewId"),
                    reviewRow.getString("content"),
                    reviewRow.getBoolean("isPositive"),
                    reviewRow.getLong("user_id"),
                    reviewRow.getLong("film_id"),
                    reviewRow.getInt("useful")
            );
            return Optional.of(review);
        } else {
            log.info("Отзыв с идентификатором {} не найден.", id);
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
        String sql = "update REVIEWS set CONTENT  = ?, ISPOSITIVE = ? where REVIEWID = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getId());

        return getReview(review.getId());
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM REVIEWS where reviewId = ?";
        if (jdbcTemplate.update(sql, id) == 0) {
            throw new ReviewNotFoundException();
        }
    }

    @Override
    public void addLike(Long reviewID, Long userId, boolean isPositive) {
        String sql = "INSERT INTO reviewLikes (REVIEWID,USER_ID,ISPOSITIVE) VALUES (?,?,?)";
        if (jdbcTemplate.update(sql, reviewID, userId, isPositive) == 1) {
            String sqlUpdateReview = "UPDATE REVIEWS SET USEFUL = USEFUL " + (isPositive ? "+" : "-") + " 1 " +
                    "WHERE REVIEWID = ?";
            jdbcTemplate.update(sqlUpdateReview, reviewID);
        }
    }

    @Override
    public void removeLike(Long reviewID, Long userId, boolean isPositive) {
        String sql = "DELETE FROM  reviewLikes WHERE REVIEWID =? AND USER_ID = ? AND ISPOSITIVE = ?";
        if (jdbcTemplate.update(sql, reviewID, userId, isPositive) == 1) {
            String sqlUpdateReview = "UPDATE REVIEWS SET USEFUL = USEFUL " + (isPositive ? "-" : "+") + " 1 " +
                    "WHERE REVIEWID = ?";
            jdbcTemplate.update(sqlUpdateReview, reviewID);
        }
    }

}
