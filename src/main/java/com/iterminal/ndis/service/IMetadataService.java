package com.iterminal.ndis.service;

import com.iterminal.ndis.model.User;
import com.iterminal.ndis.dto.RequestChanagePasswordDto;
import com.iterminal.exception.CustomException;

import java.util.List;


public interface IMetadataService {

    enum StatusValue {
        Active,
        Inactive
    }

    public List<User> getUserListByStatus(String status) throws CustomException;

    public void changePassword(RequestChanagePasswordDto request) throws CustomException;

}
