package com.iterminal.exception;


public class AlreadyExistException extends CustomException {

    public AlreadyExistException() {
        super(ALREADY_EXISTS, "Already Exist");
    }

    public AlreadyExistException(String message) {
        super(ALREADY_EXISTS, message);
    }

}
