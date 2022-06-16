package com.iterminal.exception;



public class DuplicateRecordException extends CustomException {

    public DuplicateRecordException() {
        super(ALREADY_EXISTS, "Record already exists");
    }

    public DuplicateRecordException(String errorMessage) {
        super(ALREADY_EXISTS, errorMessage);
    }

}
