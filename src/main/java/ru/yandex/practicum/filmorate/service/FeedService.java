package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;

import java.util.List;

@Slf4j
@Service
public class FeedService {

    private final FeedStorage feedStorage;

    public FeedService(FeedStorage feedStorage) {
        this.feedStorage = feedStorage;
    }

    public List<Feed> getFeeds(Long userId) {
        return feedStorage.getFeed(userId);
    }

    public void addFeed(Long userId, Long eventId, String operation, String eventType) {
        feedStorage.addFeed(userId, eventId, operation, eventType);
    }
}
