/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.service;

import com.iterminal.ndis.dto.TokenRefreshResponseDto;
import com.iterminal.ndis.dto.response.UserDto;
import com.iterminal.exception.CustomException;
import com.iterminal.ndis.model.User;


public interface IAuthService {

    public User login(String username, String password) throws CustomException;

    public void logout(String username) throws CustomException;

    public TokenRefreshResponseDto refreshToken(String token, String refreshToken) throws CustomException;

    public UserDto authenticate(String token) throws CustomException;

    public boolean authorization(String username, String featureAction) throws CustomException;

}
