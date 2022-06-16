package com.iterminal.ndis.service;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.searchfilters.RequestListCountDto;
import com.iterminal.searchfilters.RequestListDto;

import java.util.List;

public interface ISuperService<T, ID> {

    public T save(T t) throws CustomException;

    public T findById(ID id) throws CustomException;

    public List<T> getAll() throws CustomException;

    public long getCount(RequestListCountDto requestListCount) throws CustomException;

    public PaginationDto<T> getPaginatedList(RequestListDto requestList) throws CustomException;

    public void delete(ID id) throws CustomException;

    public T update(ID id, T t) throws CustomException;
}
