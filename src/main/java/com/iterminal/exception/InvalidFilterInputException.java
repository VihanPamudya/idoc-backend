package com.iterminal.exception;


public class InvalidFilterInputException extends CustomException {

    public InvalidFilterInputException() {
        super(INVALID_JSON_INPUT, "Invalid Filter Input");
    }

    public InvalidFilterInputException(String message) {
        super(INVALID_JSON_INPUT, message);
    }

}
