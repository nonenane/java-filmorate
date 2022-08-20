package ru.yandex.practicum.filmorate.storage.feed;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {

    List<Feed> getFeed(Long userId);
    void addFeed(Long userId,Long eventId,String operation,String eventType);
}
