package ru.yandex.practicum.filmorate.myAnnotation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class myDateValidator implements ConstraintValidator<myDate, LocalDate> {

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value.isBefore(LocalDate.of(1895, 12, 28))) {
            return false;
        }
        return true;
    }
}
