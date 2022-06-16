 package com.iterminal.ndis.service;

import com.iterminal.ndis.dto.RequestChanagePasswordDto;
import com.iterminal.ndis.dto.UserRequestDto;
import com.iterminal.ndis.dto.response.NotificationDto;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.dto.response.UserBasicDto;
import com.iterminal.ndis.dto.response.UserDto;
import com.iterminal.exception.CustomException;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.model.UserHistory;
import com.iterminal.searchfilters.RequestListCountDto;
import com.iterminal.searchfilters.RequestListDto;
import java.util.List;


public interface IUserService {

    enum Status {
        Active,
        Inactive,
        Block
    }

    public UserDto create(UserRequestDto user) throws CustomException;

    public long getCount(RequestListCountDto requestListCount) throws CustomException;

    public PaginationDto<User> getPaginatedList(RequestListDto requestList) throws CustomException;

    public List<User> getAll() throws CustomException;

    public void delete(String epfNumber) throws CustomException;

    public UserDto update(String epfNumber, UserRequestDto user) throws CustomException;

    public User inactive(String epfNumber) throws CustomException;

    public User active(String epfNumber) throws CustomException;

    public List<UserHistory> getUserHistory(String epfNumber) throws CustomException;

    public void changePassword(RequestChanagePasswordDto requestChanagePassword) throws CustomException;

    public void resetPassword(String epfNumber) throws CustomException;

    public UserDto updateUserProfile(String epfNumber, User user) throws CustomException;

    public UserDto findUserById(String userName) throws CustomException;

    public String getUserFullNameByUserName(String userName) throws CustomException;

    public UserDto convertUserToUserResponseDto(User user) throws CustomException;

    public UserBasicDto convertUserToUserBasicResponseDto(User user) throws CustomException;

    public List<NotificationDto> getNotificationList() throws CustomException;

    public long getNotificationCount() throws CustomException;

    public void readNotification(Long notificationId) throws CustomException;

    public List<User> getUserListByStatus(String status) throws CustomException;
}
