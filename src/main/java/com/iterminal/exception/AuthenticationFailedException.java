package com.iterminal.exception;


public class AuthenticationFailedException extends CustomException {

    public AuthenticationFailedException() {
        super(AUTHENTICATION_FAILED, "Request authentication failed.");
    }

    public AuthenticationFailedException(String message) {
        super(AUTHENTICATION_FAILED, message);
    }
}
