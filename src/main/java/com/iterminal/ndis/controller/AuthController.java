/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.controller;

import com.iterminal.ndis.dto.RequestAuthorizationDto;
import com.iterminal.ndis.dto.RequestLogInDto;
import com.iterminal.ndis.dto.TokenRefreshRequestDto;
import com.iterminal.ndis.dto.TokenRefreshResponseDto;
import com.iterminal.ndis.dto.response.UserDto;
import com.iterminal.exception.CustomException;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.util.ErrorResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.iterminal.ndis.service.IAuthService;
import com.iterminal.ndis.service.IUserService;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/auth")
public class AuthController {

    private final IUserService userService;
    private final IAuthService authService;

    @Autowired
    public AuthController(IAuthService authService,
            IUserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<UserDto> login(@RequestBody RequestLogInDto request) {

        try {
            User loginUser = authService.login(request.getUsername(), request.getPassword());
            UserDto userDto = userService.findUserById(loginUser.getEpfNumber());
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Authorization", loginUser.getToken());
            responseHeaders.set("RefreshToken", loginUser.getRefreshToken());
            responseHeaders.add("Access-Control-Expose-Headers", "Authorization,RefreshToken");
            //refreshToken
            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(userDto);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PutMapping(value = "/logout/{username}")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token, @PathVariable("username") String username) {

        try {
            authService.logout(username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PostMapping(value = "/reset-password/{username}")
    public ResponseEntity<Void> resetPassword(@PathVariable("username") String username) {
        try {
            userService.resetPassword(username);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<UserDto> authenticate(@RequestHeader("Authorization") String token) {

        try {
            UserDto userDto = authService.authenticate(token);

            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PostMapping(value = "/authorization")
    public ResponseEntity<Void> authorization(@RequestBody RequestAuthorizationDto request) {

        try {
            authService.authorization(request.getUsername(), request.getFeatureAction());

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PostMapping(value = "/refresh-token")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(@RequestHeader("Authorization") String token, @RequestBody TokenRefreshRequestDto request) {

        try {
            TokenRefreshResponseDto response = authService.refreshToken(token, request.getRefreshToken());

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

}
