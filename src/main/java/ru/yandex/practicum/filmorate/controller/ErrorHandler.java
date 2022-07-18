package ru.yandex.practicum.filmorate.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.*;


@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleValidationException(final ValidationException e) {
        log.info( String.format("error: Ошибка валидации, некорректный параметр %s", e.getParameter()));
        return new ResponseEntity<>(
                String.format("Ошибка валидации, некорректный параметр %s", e.getParameter()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleMethodArgumentNotValid(final MethodArgumentNotValidException e) {

        String defaultMessage = e.getMessage()
                .split("default message")[1]
                .split("]")[0]
                .substring(2);

        log.info(String.format("error: Ошибка валидации, некорректный параметр %s", defaultMessage));

        return new ResponseEntity<>(
                String.format("Ошибка валидации, некорректный параметр %s", defaultMessage),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleNotFoundException(final NotFoundException e) {
        log.info(String.format("error: %s c таким идентификатором не найден.", e.getObjectType()));
        return new ResponseEntity<>(
                String.format("%s c таким идентификатором не найден.", e.getObjectType()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleAlreadyFriendsException(final AlreadyFriendsException e) {
        log.info("error: Пользователи уже являются друзьями.");
        return new ResponseEntity<>(
                "Пользователи уже являются друзьями.",
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleThrowable(final Throwable e) {
        log.info("error: Произошла непредвиденная ошибка " + e.getClass());
        return new ResponseEntity<>(
                "Произошла непредвиденная ошибка " + e.getClass(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}