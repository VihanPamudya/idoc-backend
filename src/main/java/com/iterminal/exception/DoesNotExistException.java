package com.iterminal.exception;

/**
 *
 * @author suranga Created date: Feb 20, 2022
 */
public class DoesNotExistException extends CustomException {

    public DoesNotExistException() {
        super(DOES_NOT_EXIST, "Does Not Exist");
    }

    public DoesNotExistException(String message) {
        super(DOES_NOT_EXIST, message);
    }

}
