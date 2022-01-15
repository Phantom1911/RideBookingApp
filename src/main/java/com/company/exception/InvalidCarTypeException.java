package com.company.exception;

public class InvalidCarTypeException extends RuntimeException {
    public InvalidCarTypeException(final String message) {
        super(message);
    }
}
