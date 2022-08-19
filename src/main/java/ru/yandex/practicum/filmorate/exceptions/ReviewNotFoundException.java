package ru.yandex.practicum.filmorate.exceptions;

public class ReviewNotFoundException extends NotFoundException{
    public ReviewNotFoundException() {
        super("Review");
    }
}
