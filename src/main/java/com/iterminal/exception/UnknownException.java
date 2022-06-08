package com.iterminal.exception;


public class UnknownException extends CustomException {

    public UnknownException() {
        super(UNKNOWN, "Unknown Exception");
    }

    public UnknownException(String errorMessage) {
        super(UNKNOWN, errorMessage);
    }
}
