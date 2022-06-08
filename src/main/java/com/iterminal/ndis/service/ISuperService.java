/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.service;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.searchfilters.RequestListCountDto;
import com.iterminal.searchfilters.RequestListDto;

import java.util.List;

/**
 * @author navishka Created date: Aug 18, 2021
 * @param <T>
 * @param <ID>
 */
public interface ISuperService<T, ID> {

    public T save(T t) throws CustomException;

    public T findById(ID id) throws CustomException;

    public List<T> getAll() throws CustomException;

    public long getCount(RequestListCountDto requestListCount) throws CustomException;

    public PaginationDto<T> getPaginatedList(RequestListDto requestList) throws CustomException;

    public void delete(ID id) throws CustomException;

    public T update(ID id, T t) throws CustomException;
}
