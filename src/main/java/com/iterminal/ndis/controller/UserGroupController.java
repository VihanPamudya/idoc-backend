package com.iterminal.ndis.controller;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.CountResultDto;
import com.iterminal.ndis.dto.UserGroupRequestDto;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.model.UserGroup;
import com.iterminal.ndis.service.IUserGroupService;
import com.iterminal.ndis.dto.response.UserGroupDto;
import com.iterminal.ndis.util.ErrorResponseUtil;
import com.iterminal.searchfilters.RequestListCountDto;
import com.iterminal.searchfilters.RequestListDto;
import org.hibernate.mapping.Any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("group-management/group")
public class UserGroupController {
    private final IUserGroupService userGroupService;

    @Autowired
    public UserGroupController(IUserGroupService userGroupService) { this.userGroupService = userGroupService; }

    @GetMapping("/view/{id}")
    public ResponseEntity<UserGroupDto> getGroup(@PathVariable("id") Long group_Id) {
        try {
            UserGroupDto userGroupDto = userGroupService.findGroupById(group_Id);
            return new ResponseEntity<>(userGroupDto, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<UserGroup>> getAllGroups() {

        try {
            List<UserGroup> groupList = userGroupService.getAll();
            return new ResponseEntity<>(groupList, HttpStatus.OK);

        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PostMapping(value = "/list")
    public ResponseEntity<PaginationDto<UserGroup>> getGroups(@RequestBody RequestListDto requestList) {
        try {
            PaginationDto<UserGroup> groupList = userGroupService.getPaginatedList(requestList);
            return new ResponseEntity<>(groupList, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PostMapping(value = "/add")
    public ResponseEntity<UserGroupDto> createGroup(@RequestBody UserGroupRequestDto group) {

        try {
            UserGroupDto savedGroup = userGroupService.create(group);
            return new ResponseEntity<>(savedGroup, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PutMapping(value = "/modify/{id}")
    public ResponseEntity<UserGroupDto> updateUserGroup(@PathVariable("id") long group_Id, @RequestBody UserGroupRequestDto group) {
        try {
            UserGroupDto updatedGroup = userGroupService.update(group_Id, group);
            return new ResponseEntity<>(updatedGroup, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @DeleteMapping(value = "/delete/{id}/")
    public ResponseEntity<Void> deleteUserGroup(@PathVariable("id") long group_Id) {
        try {
            userGroupService.delete(group_Id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PutMapping(value = "/{id}/inactive")
    public ResponseEntity<UserGroup> inactive(@PathVariable("id") long group_id) {

        try{
            UserGroup inactivatedUseGroup  = userGroupService.inactive(group_id);
            return new ResponseEntity<UserGroup>(inactivatedUseGroup, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @GetMapping(value = "/count")
    public ResponseEntity<CountResultDto> getCount() {

        try {
            CountResultDto countDro = new CountResultDto();
            long count = userGroupService.getCount(null);
            countDro.setCount(count);
            return new ResponseEntity<>(countDro, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PostMapping(value = "/count")
    public ResponseEntity<CountResultDto> getCount(@RequestBody RequestListCountDto requestListCount) {

        try {
            CountResultDto countDro = new CountResultDto();
            long count = userGroupService.getCount(requestListCount);
            countDro.setCount(count);
            return new ResponseEntity<>(countDro, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }





}
