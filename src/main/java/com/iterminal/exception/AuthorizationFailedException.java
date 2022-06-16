package com.iterminal.exception;


public class AuthorizationFailedException extends CustomException {

    public AuthorizationFailedException() {
        super(AUTHORIZATION_FAILED, "Request authorization failed.");
    }

    public AuthorizationFailedException(String message) {
        super(AUTHORIZATION_FAILED, message);
    }

}
