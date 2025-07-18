package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NotAvailableException extends RuntimeException {
    public NotAvailableException(String message) {
        super(message);
    }
}
