package com.iterminal.exception;


public class InvalidInputException extends CustomException {

    public InvalidInputException() {
        super(INVALID_INPUT, "Invalid Input");
    }

    public InvalidInputException(String message) {
        super(INVALID_INPUT, message);
    }
}
