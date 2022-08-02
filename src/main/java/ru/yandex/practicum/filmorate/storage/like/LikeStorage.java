package ru.yandex.practicum.filmorate.storage.like;

public interface LikeStorage {

    void addLike(Long idFilm, Long idUser);

    void removeLike(Long idFilm, Long idUser);
}
