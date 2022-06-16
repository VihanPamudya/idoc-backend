package com.iterminal.ndis.controller;


import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.response.UserAndUserGroupsDto;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.service.ISearchService;
import com.iterminal.ndis.service.IUserGroupService;
import com.iterminal.ndis.service.IUserService;
import com.iterminal.ndis.util.ErrorResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("search-management/user-user-group")
public class SearchController {
    private final ISearchService searchService;

    @Autowired
    public SearchController(
            ISearchService searchService
    ){
        this.searchService = searchService;
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<UserAndUserGroupsDto>> getUsersAndUserGroups (@RequestParam String searchValue){

        try{
            List<UserAndUserGroupsDto> userAndUserGroupsDtoList = searchService.getUsersAndUserGroups(searchValue);
            return new ResponseEntity<>(userAndUserGroupsDtoList, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }
}
