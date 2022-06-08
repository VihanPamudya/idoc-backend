/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.service.impl;

import com.iterminal.ndis.service.IUserService;
import com.iterminal.ndis.service.IMetadataService;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.repository.SqlCustomQueryRepository;
import com.iterminal.ndis.repository.IActionTypeRepository;
import com.iterminal.ndis.dto.RequestChanagePasswordDto;
import com.iterminal.exception.CustomException;
import com.iterminal.exception.UnknownException;
import com.iterminal.ndis.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class MetadataService implements IMetadataService {

    private final SqlCustomQueryRepository sqlCustomQueryRepository;
    private final IActionTypeRepository actionTypeRepository;
    private final IUserService userService;

    @Autowired
    public MetadataService(
            SqlCustomQueryRepository sqlCustomQueryRepository,
            IActionTypeRepository actionTypeRepository,
            IUserService userService) {

        this.sqlCustomQueryRepository = sqlCustomQueryRepository;
        this.actionTypeRepository = actionTypeRepository;
        this.userService = userService;
    }

    @Override
    public List<User> getUserListByStatus(String status) throws CustomException {
        try {
            List<User> foundUserList = userService.getUserListByStatus(status);
            return foundUserList;
        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UnknownException(e.getMessage());
        }
    }

    @Override
    public void changePassword(RequestChanagePasswordDto request) throws CustomException {
        try {

            String userName = DataUtil.getUserName();

            if (!userName.equals(request.getUsername())) {
                throw new CustomException(CustomException.INVALID_JWT_TOKEN, "Invalid JWT token.");
            }

            userService.changePassword(request);

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }
}
