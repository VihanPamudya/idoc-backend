/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iterminal.exception;

/**
 *
 * @author suranga Created date: Feb 20, 2022
 */
public class InvalidInputException extends CustomException {

    public InvalidInputException() {
        super(INVALID_INPUT, "Invalid Input");
    }

    public InvalidInputException(String message) {
        super(INVALID_INPUT, message);
    }
}
