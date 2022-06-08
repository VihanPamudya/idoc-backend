/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.controller;

import com.google.gson.Gson;
import com.iterminal.ndis.dto.CountResultDto;
import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.model.Role;
import com.iterminal.ndis.model.RoleHistory;
import com.iterminal.ndis.util.ErrorResponseUtil;
import com.iterminal.searchfilters.RequestListDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.iterminal.ndis.service.IRoleService;
import com.iterminal.searchfilters.RequestListCountDto;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/user-management/role-and-permission")
public class RoleController {

    Gson gson = new Gson();
    private final IRoleService roleService;

    @Autowired
    public RoleController(IRoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping(value = "/view/{id}")
    public ResponseEntity<Role> getRole(@PathVariable("id") Long id) {

        try {

            Role role = roleService.findById(id);
            return new ResponseEntity<>(role, HttpStatus.OK);

        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<Role>> getAllRoles() {

        try {
            List<Role> roles = roleService.getAll();
            return new ResponseEntity<>(roles, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @GetMapping(value = "/count")
    public ResponseEntity<CountResultDto> getCount() {

        try {
            CountResultDto countDto = new CountResultDto();
            long count = roleService.getCount(null);
            countDto.setCount(count);
            return new ResponseEntity<>(countDto, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PostMapping(value = "/list")
    public ResponseEntity<PaginationDto<Role>> getRoles(@RequestBody RequestListDto requestList) {
        try {
            PaginationDto<Role> roles = roleService.getPaginatedList(requestList);

            return new ResponseEntity<>(roles, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PostMapping(value = "/count")
    public ResponseEntity<CountResultDto> getCount(@RequestBody RequestListCountDto requestListCount) {

        try {
            CountResultDto countDto = new CountResultDto();
            long count = roleService.getCount(requestListCount);
            countDto.setCount(count);
            return new ResponseEntity<>(countDto, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PostMapping(value = "/add")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {

        try {
            Role savedRole = roleService.save(role);
            return new ResponseEntity<>(savedRole, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PutMapping(value = "/modify/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable("id") Long id, @RequestBody Role role) {

        try {

            Role updatedRole = roleService.update(id, role);

            return new ResponseEntity<>(updatedRole, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") Long id) {

        try {
            roleService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PutMapping(value = "/{id}/active")
    public ResponseEntity<Role> active(@PathVariable("id") Long id) {

        try {
            Role updatedRole = roleService.active(id);
            return new ResponseEntity<>(updatedRole, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PutMapping(value = "/{id}/inactive")
    public ResponseEntity<Role> inactive(@PathVariable("id") Long id) {

        try {
            Role updatedRole = roleService.inactive(id);
            return new ResponseEntity<>(updatedRole, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @GetMapping(value = "/{id}/history")
    public ResponseEntity<List<RoleHistory>> getRoleHistory(@PathVariable("id") Long id) {

        try {
            List<RoleHistory> roleHistory = roleService.getRoleHistory(id);
            return new ResponseEntity<>(roleHistory, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PutMapping(value = "/{id}/lock")
    public ResponseEntity<Void> lock(@PathVariable("id") Long id) {

        try {
            roleService.lock(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PutMapping(value = "/{id}/unlock")
    public ResponseEntity<Void> unlock(@PathVariable("id") Long id) {

        try {
            roleService.unlock(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

//    private RoleDto getJsonRole(Role role) {
//
//        RoleDto roleDto = gson.fromJson(gson.toJson(role), RoleDto.class);
//        if (!role.getPermissionData().isEmpty()) {
//            PermissionDto permissions = gson.fromJson(role.getPermissionData(), PermissionDto.class);
//            roleDto.setPermissions(permissions);
//            roleDto.setPermissionData(null);
//        }
//        return roleDto;
//
//    }

    /*
        @GetMapping(value = "/{id}")
    public ResponseEntity<Role> getRole(@PathVariable("id") Long id) {

        try {

            Role role = roleService.findById(id);
            return new ResponseEntity<>(role, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }
    
       @PostMapping(value = "/")
    public ResponseEntity<Role> createRole(@RequestBody Role role) {

        try {
            Role savedRole = roleService.save(role);
            return new ResponseEntity<>(savedRole, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable("id") Long id, @RequestBody Role role) {

        try {
            Role updatedRole = roleService.update(id, role);
            return new ResponseEntity<>(updatedRole, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }
     */
}
