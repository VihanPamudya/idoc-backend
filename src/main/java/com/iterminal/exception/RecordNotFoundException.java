/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */

package com.iterminal.exception;



public class RecordNotFoundException extends CustomException {

    public RecordNotFoundException() {
        super(RECORD_NOT_FOUND, "Record not found");
    }

    public RecordNotFoundException(String message) {
        super(RECORD_NOT_FOUND, message);
    }
}
