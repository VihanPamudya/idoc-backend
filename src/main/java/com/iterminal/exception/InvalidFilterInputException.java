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
public class InvalidFilterInputException extends CustomException {

    public InvalidFilterInputException() {
        super(INVALID_JSON_INPUT, "Invalid Filter Input");
    }

    public InvalidFilterInputException(String message) {
        super(INVALID_JSON_INPUT, message);
    }

}
