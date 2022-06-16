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
