/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.service;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.model.Organization;

import java.util.List;


public interface IOrganizationService {

    Organization findById(Long id) throws CustomException;

    List<Organization> getAll() throws CustomException;

}
