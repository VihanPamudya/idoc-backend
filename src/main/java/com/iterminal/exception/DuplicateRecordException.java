/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */

package com.iterminal.exception;



public class DuplicateRecordException extends CustomException {

    public DuplicateRecordException() {
        super(ALREADY_EXISTS, "Record already exists");
    }

    public DuplicateRecordException(String errorMessage) {
        super(ALREADY_EXISTS, errorMessage);
    }

}
