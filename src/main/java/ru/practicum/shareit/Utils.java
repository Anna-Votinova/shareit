package ru.practicum.shareit;

import javax.validation.ValidationException;

public class Utils {

    public static void checkFromAndSize(int from, int size) {
        if (size < 1 || from < 0) {
            throw new ValidationException("Размер страницы не может быть меньше 1, " +
                    "а страница, с которой нужно начать поиск - меньше 0");
        }
    }
}
