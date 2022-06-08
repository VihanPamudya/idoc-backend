package com.iterminal.exception;

import lombok.Data;



@Data
public class CustomException extends Exception {

    private int errorCode;

    public final static int ALREADY_EXISTS = 1;
    public final static int RECORD_NOT_FOUND = 2;
    public final static int INVALID_INPUT = 3;
    public final static int DOES_NOT_EXIST = 4;
    public final static int AUTHENTICATION_FAILED = 5;
    public final static int AUTHORIZATION_FAILED = 6;
    public final static int INVALID_JWT_SIGNATURE = 7;
    public final static int INVALID_JWT_TOKEN = 8;
    public final static int EXPIRED_JWT_EXCEPTION = 9;
    public final static int UNSUPPORTED_JWT_EXCEPTION = 10;
    public final static int INVALID_JSON_INPUT = 11;
    public final static int INVALID_RELATIONSHIP = 12;
    public final static int CONNECTIVITY_ERROR = 13;

    public final static int UNKNOWN = 100;

    public CustomException(String message) {
        super(message);
        this.errorCode = 000;
    }

    public CustomException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
