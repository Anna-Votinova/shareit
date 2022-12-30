package ru.practicum.shareit.exceptions;

public class UnknownStateException extends RuntimeException {

    public UnknownStateException(String state) {
        super(state);
    }
}
