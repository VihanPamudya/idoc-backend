/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.util;
import com.iterminal.ndis.dto.ErrorResponseDto;
import com.iterminal.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 *
 * @author suranga
 */
public class ErrorResponseUtil {
    
    public static ResponseEntity errorResponse(CustomException ex) {

        ErrorResponseDto response = new ErrorResponseDto();
        response.setErrorCode(ex.getErrorCode());
        response.setMessage(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }
}
