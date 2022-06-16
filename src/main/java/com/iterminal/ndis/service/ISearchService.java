package com.iterminal.ndis.service;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.response.UserAndUserGroupsDto;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.model.UserGroup;

import java.util.List;

public interface ISearchService {
    List<UserAndUserGroupsDto> getUsersAndUserGroups(String searchValue) throws CustomException;

    UserAndUserGroupsDto convertUserToCommon(User user) throws CustomException;

    UserAndUserGroupsDto convertGroupToCommon(UserGroup userGroup) throws CustomException;
}
