package ru.yandex.practicum.filmorate.storage.feed.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

@Slf4j
@Component
@Primary
public class FeedDbStorage implements FeedStorage {

    private final JdbcTemplate jdbcTemplate;

    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Feed> getFeed(Long userId) {

        String sql = "select  f.eventId, f.timeEvent, userId, entityId, eventType, operation " +
                "from feeds as f" +
                " where f.USERID = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFeed(rs),userId);
    }

    private Feed makeFeed(ResultSet feedRows) throws SQLException {
        Long eventId = feedRows.getLong("eventId");
        return new Feed(eventId,
                feedRows.getTimestamp("timeEvent").getTime(),
                feedRows.getLong("userId"),
                feedRows.getLong("entityId"),
                feedRows.getString("eventType"),
                feedRows.getString("operation"));
    }

    @Override
    public void addFeed(Long userId, Long eventId, String operation, String eventType) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("feeds")
                .usingGeneratedKeyColumns("eventId");
        simpleJdbcInsert.execute(toMapFeed(userId, eventId, operation, eventType));
    }

    private Map<String, Object> toMapFeed(Long userId, Long eventId, String operation, String eventType) {
        Map<String, Object> values = new HashMap<>();
        values.put("timeEvent", Instant.now());
        values.put("userId", userId);
        values.put("entityId", eventId);
        values.put("eventType", eventType);
        values.put("operation", operation);
        return values;
    }
}
