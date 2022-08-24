package ru.yandex.practicum.filmorate.exceptions;

public class DirectorNotFoundException extends NotFoundException {
    public DirectorNotFoundException() {
        super("Director");
    }
}

