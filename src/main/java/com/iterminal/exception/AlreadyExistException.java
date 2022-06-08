package com.iterminal.exception;

/**
 *
 * @author suranga Created date: Feb 20, 2022
 */
public class AlreadyExistException extends CustomException {

    public AlreadyExistException() {
        super(ALREADY_EXISTS, "Already Exist");
    }

    public AlreadyExistException(String message) {
        super(ALREADY_EXISTS, message);
    }

}
