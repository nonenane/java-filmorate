package ru.yandex.practicum.filmorate.storage.users.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "select * from users";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser( rs));
    }

    public static User makeUser(ResultSet userRows) throws SQLException {
        return new User(userRows.getLong("USER_ID"),
                userRows.getString("EMAIL"),
                userRows.getString("LOGIN"),
                userRows.getString("NAME"),
                userRows.getDate("BIRTHDAY").toLocalDate());
    }

    @Override
    public Optional<User> getUser(Long id) {
        //return Optional.empty();
        SqlRowSet userRow = jdbcTemplate.queryForRowSet("select * from users where USER_ID = ?", id);

        if (userRow.next()) {
            log.info("Найден фильм: {} {}", userRow.getString("USER_ID"), userRow.getString("name"));
            User user = new User(userRow.getLong("USER_ID"),
                    userRow.getString("EMAIL"),
                    userRow.getString("LOGIN"),
                    userRow.getString("NAME"),
                    userRow.getDate("BIRTHDAY").toLocalDate());
            return Optional.of(user);
        } else {
            log.info("Фильм с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> create(User user) {
        log.info(user.toString());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("USER_ID");

        Long id = simpleJdbcInsert.executeAndReturnKey(toMapUser(user)).longValue();
        return getUser(id);
    }

    private Map<String, Object> toMapUser(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());
        return values;
    }

    @Override
    public Optional<User> update(User user) {
        String sql = "update users set email = ?, login = ?, name = ?, birthday = ? where user_id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        return getUser(user.getId());
    }

    @Override
    public void removeByUserId(Long userId) throws UserNotFoundException {

        jdbcTemplate.update("delete from FRIENDS where USER_ID=?", userId);
        jdbcTemplate.update("delete from FRIENDS where FRIEND_ID=?", userId);
        jdbcTemplate.update("delete from LIKES where USER_ID=?", userId);
        String sqlString = "delete from USERS where USER_ID=?";
        if (jdbcTemplate.update(sqlString, userId) == 0) {
            throw new UserNotFoundException();
        }
    }
}
