package com.company.exception;

public class CarAlreadyPresentException extends RuntimeException {

    public CarAlreadyPresentException(final String message) {
        super(message);
    }

}
