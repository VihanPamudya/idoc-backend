package com.iterminal.exception;


public class DoesNotExistException extends CustomException {

    public DoesNotExistException() {
        super(DOES_NOT_EXIST, "Does Not Exist");
    }

    public DoesNotExistException(String message) {
        super(DOES_NOT_EXIST, message);
    }

}
