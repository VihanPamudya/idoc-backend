package com.iterminal.exception;



public class RecordNotFoundException extends CustomException {

    public RecordNotFoundException() {
        super(RECORD_NOT_FOUND, "Record not found");
    }

    public RecordNotFoundException(String message) {
        super(RECORD_NOT_FOUND, message);
    }
}
