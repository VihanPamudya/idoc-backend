package com.iterminal.ndis.service.impl;


import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.dto.response.UserAndUserGroupsDto;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.model.UserGroup;
import com.iterminal.ndis.service.ISearchService;
import com.iterminal.ndis.service.IUserGroupService;
import com.iterminal.ndis.service.IUserService;
import com.iterminal.searchfilters.FilterOperator;
import com.iterminal.searchfilters.PropertyFilterDto;
import com.iterminal.searchfilters.RequestListDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SearchService implements ISearchService {

    private final IUserService userService;
    private final IUserGroupService userGroupService;

    private final Integer NumberOfResults = 10;

    public SearchService(
            IUserService userService,
            IUserGroupService userGroupService
    ){
        this.userService = userService;
        this.userGroupService = userGroupService;
    }


    @Override
    public List<UserAndUserGroupsDto> getUsersAndUserGroups(String searchValue) throws CustomException {

        PropertyFilterDto userPropertyFilterDto = new PropertyFilterDto();
        PropertyFilterDto userGroupPropertyFilterDto = new PropertyFilterDto();

        userPropertyFilterDto.setProperty("user_name");
        userPropertyFilterDto.setValue(searchValue);
        userPropertyFilterDto.setOperator(FilterOperator.LIKE);

        userGroupPropertyFilterDto.setProperty("group_name");
        userGroupPropertyFilterDto.setValue(searchValue);
        userGroupPropertyFilterDto.setOperator(FilterOperator.LIKE);

        ArrayList<PropertyFilterDto> propertyFilterDtoListUsers = new ArrayList<>();
        propertyFilterDtoListUsers.add(userPropertyFilterDto);

        ArrayList<PropertyFilterDto> propertyFilterDtoListGroups = new ArrayList<>();
        propertyFilterDtoListGroups.add(userGroupPropertyFilterDto);

        RequestListDto userRequestListDto = new RequestListDto();
        RequestListDto userGroupRequestListDto = new RequestListDto();

        userRequestListDto.setLimit(NumberOfResults);
        userRequestListDto.setFilterData(propertyFilterDtoListUsers);

        userGroupRequestListDto.setLimit(NumberOfResults);
        userGroupRequestListDto.setFilterData(propertyFilterDtoListGroups);

        PaginationDto<User> userList = userService.getPaginatedList(userRequestListDto);
        PaginationDto<UserGroup> groupList = userGroupService.getPaginatedList(userGroupRequestListDto);

        if(!((userList.getTotalSize() == NumberOfResults/2 && groupList.getTotalSize() == NumberOfResults/2) || (userList.getTotalSize() < NumberOfResults/2 && groupList.getTotalSize() < NumberOfResults/2))){
            if(userList.getTotalSize() < 5){
                userGroupRequestListDto.setLimit((int) (NumberOfResults - userList.getTotalSize()));
                groupList = userGroupService.getPaginatedList(userGroupRequestListDto);
            } else {
                userRequestListDto.setLimit((int) (NumberOfResults - groupList.getTotalSize()));
                userList = userService.getPaginatedList(userRequestListDto);
            }
        }

        List<UserAndUserGroupsDto> userAndUserGroupsDtoList = new ArrayList<>();

        for(User user:userList.getData()){
            userAndUserGroupsDtoList.add(convertUserToCommon(user));
        }

        for(UserGroup userGroup:groupList.getData()){
            userAndUserGroupsDtoList.add(convertGroupToCommon(userGroup));
        }

        return userAndUserGroupsDtoList;
    }

    @Override
    public UserAndUserGroupsDto convertUserToCommon(User user) throws CustomException{
        UserAndUserGroupsDto userAndUserGroupsDto = new UserAndUserGroupsDto();

        userAndUserGroupsDto.setUser(true);
        userAndUserGroupsDto.setName(user.getUserName());
        userAndUserGroupsDto.setId(user.getEpfNumber());

        return userAndUserGroupsDto;
    }

    @Override
    public UserAndUserGroupsDto convertGroupToCommon(UserGroup userGroup) throws CustomException{
        UserAndUserGroupsDto userAndUserGroupsDto = new UserAndUserGroupsDto();

        userAndUserGroupsDto.setUser(false);
        userAndUserGroupsDto.setName(userGroup.getName());
        userAndUserGroupsDto.setId(String.valueOf(userGroup.getId()));

        return userAndUserGroupsDto;
    }
}
