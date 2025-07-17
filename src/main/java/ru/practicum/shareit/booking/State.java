package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.InvalidUserInputException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State fromString(String stateStr) {
        try {
            return State.valueOf(stateStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidUserInputException("Нет такого статуса брони: " + stateStr);
        }
    }
}
