package com.iterminal.ndis.service;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.UserGroupRequestDto;
import com.iterminal.ndis.dto.UserRequestDto;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.dto.response.UserDto;
import com.iterminal.ndis.dto.response.UserGroupDto;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.model.UserGroup;
import com.iterminal.searchfilters.RequestListCountDto;
import com.iterminal.searchfilters.RequestListDto;

import java.util.List;

public interface IUserGroupService {

    enum Status {
        Active,
        Inactive
    }

    public UserGroupDto create(UserGroupRequestDto group) throws CustomException;

    public UserGroupDto findGroupById(long group_Id) throws CustomException;

    public UserGroup findUserGroupById(long group_Id) throws CustomException;

    public List<UserGroup> getAll() throws CustomException;

    public PaginationDto<UserGroup> getPaginatedList(RequestListDto requestList) throws CustomException;

    public UserGroupDto update(long group_Id, UserGroupRequestDto group) throws CustomException;

    public void delete(long group_id) throws CustomException;

    public UserGroupDto convertUserGroupToUserGroupResponseDto(UserGroup group) throws CustomException;

    public UserGroup inactive(long group_id) throws  CustomException;

    public long getCount(RequestListCountDto requestListCount) throws CustomException;

    public List<UserGroup> getUserGroupListByStatus(String status) throws CustomException;
}
