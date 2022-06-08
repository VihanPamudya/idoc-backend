package com.iterminal.exception;

/**
 *
 * @author suranga Created date: Feb 20, 2022
 */
public class AuthenticationFailedException extends CustomException {

    public AuthenticationFailedException() {
        super(AUTHENTICATION_FAILED, "Request authentication failed.");
    }

    public AuthenticationFailedException(String message) {
        super(AUTHENTICATION_FAILED, message);
    }
}
