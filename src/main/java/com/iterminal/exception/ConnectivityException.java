package com.iterminal.exception;



public class ConnectivityException extends CustomException {

    public ConnectivityException() {
        super(CONNECTIVITY_ERROR, "Connection error found");
    }

    public ConnectivityException(String message) {
        super(CONNECTIVITY_ERROR, message);
    }
}
