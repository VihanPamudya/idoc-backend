/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.service;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.model.Role;
import com.iterminal.ndis.model.RoleHistory;
import com.iterminal.ndis.model.User;
import com.iterminal.searchfilters.RequestListDto;

import java.util.List;


public interface IRoleService extends ISuperService<Role, Long> {

    public Role inactive(Long id) throws CustomException;

    public Role active(Long id) throws CustomException;

    public void lock(Long id) throws CustomException;

    public void unlock(Long id) throws CustomException;

    public List<RoleHistory> getRoleHistory(Long id) throws CustomException;

    public PaginationDto<Role> getPaginatedList(RequestListDto requestList) throws CustomException;

}
