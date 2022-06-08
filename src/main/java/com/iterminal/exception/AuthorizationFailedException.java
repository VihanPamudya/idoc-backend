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
public class AuthorizationFailedException extends CustomException {

    public AuthorizationFailedException() {
        super(AUTHORIZATION_FAILED, "Request authorization failed.");
    }

    public AuthorizationFailedException(String message) {
        super(AUTHORIZATION_FAILED, message);
    }

}
