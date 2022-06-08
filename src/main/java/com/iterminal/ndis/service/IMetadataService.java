/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.service;

import com.iterminal.ndis.model.User;
import com.iterminal.ndis.dto.RequestChanagePasswordDto;
import com.iterminal.exception.CustomException;

import java.util.List;


public interface IMetadataService {

    enum StatusValue {
        Active,
        Inactive
    }

    public List<User> getUserListByStatus(String status) throws CustomException;

    public void changePassword(RequestChanagePasswordDto request) throws CustomException;

}
