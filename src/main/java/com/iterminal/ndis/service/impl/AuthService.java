/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.service.impl;

import com.iterminal.ndis.dto.TokenRefreshResponseDto;
import com.iterminal.ndis.dto.response.UserDto;
import com.iterminal.exception.AuthenticationFailedException;
import com.iterminal.exception.AuthorizationFailedException;
import com.iterminal.exception.CustomException;
import com.iterminal.exception.DoesNotExistException;
import com.iterminal.exception.InvalidInputException;
import com.iterminal.exception.UnknownException;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.service.IUserService;
import com.iterminal.ndis.util.InputValidatorUtil;
import com.iterminal.ndis.util.MessagesAndContent;
import com.iterminal.ndis.util.PasswordEncriptor;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.iterminal.ndis.service.IAuthService;
import com.iterminal.ndis.repository.IUserRepository;
import com.iterminal.ndis.util.JwtTokenUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


@Slf4j
@Service
public class AuthService implements IAuthService {

    private final IUserRepository userRepository;
    private JwtTokenUtil jwtUtil;
    private CustomUserDetailsService userDetailsService;
    private IUserService userService;

    final String USER_STATUS_ACTIVE = "Active";
    final String USER_STATUS_INACTIVE = "Inactive";
    final String USER_STATUS_BLOCKED = "Block";

    final int MAX_NO_OF_ATTEMPTS = 3;

    @Autowired
    public AuthService(IUserRepository userRepository,
            CustomUserDetailsService userDetailsService,
            JwtTokenUtil jwtUtil,
            IUserService userService) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    public User login(String username, String password) throws CustomException {
        try {
            boolean login = false;
            long currentTime = Instant.now().getEpochSecond();

            username = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_SIGN_IN_M03, username, "Username", 50);

            password = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_SIGN_IN_M04, password, "Password", 20);

            if (!userRepository.existsById(username)) {
                throw new DoesNotExistException(MessagesAndContent.USER_SIGN_IN_M02);
            }

            User user = userRepository.getById(username);

            if (user.getStatus().equals(USER_STATUS_INACTIVE)) {
                throw new InvalidInputException(MessagesAndContent.USER_SIGN_IN_M07);
            }
            if (user.getStatus().equals(USER_STATUS_BLOCKED)) {
                throw new InvalidInputException(MessagesAndContent.USER_SIGN_IN_M08);
            }

            password = PasswordEncriptor.encriptPassword(password);

            if (user.getPassword().equals(password)) {

                final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                String temptoken = jwtUtil.generateToken(userDetails);

                String refreshToken = UUID.randomUUID().toString();

                user.setNoOfAttempts(0);
                user.setSignIn(true);
                user.setLastSignIn(currentTime);
                user.setToken(temptoken);
                user.setRefreshToken(refreshToken);
                user = userRepository.save(user);
                login = true;
            } else {
                int count = user.getNoOfAttempts() + 1;
                int rest = MAX_NO_OF_ATTEMPTS - count;
                if (rest > 0) {
                    user.setNoOfAttempts(count);
                    user = userRepository.save(user);
                    throw new InvalidInputException(MessagesAndContent.USER_SIGN_IN_M01.replace("<?>", rest + ""));
                } else {
                    user.setNoOfAttempts(0);
                    user.setStatus(USER_STATUS_BLOCKED);
                    user = userRepository.save(user);
                    throw new InvalidInputException(MessagesAndContent.USER_SIGN_IN_M05);

                }

            }

            return user;

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public void logout(String username) throws CustomException {
        try {

            username = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_SIGN_IN_M03, username, "Username", 50);

            if (!userRepository.existsById(username)) {
                throw new DoesNotExistException(MessagesAndContent.USER_SIGN_IN_M02);
            }

            User user = userRepository.getById(username);

            user.setSignIn(false);
            user.setToken("");
            user.setRefreshToken("");
            user = userRepository.save(user);

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public UserDto authenticate(String authorizationHeader) throws CustomException {

        String userName = null;

        try {

            String jwtToken;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                jwtToken = authorizationHeader.substring(7);

            } else {
                jwtToken = authorizationHeader;
            }

            if (jwtToken != null) {

                try {
                    userName = jwtUtil.getUsernameFromToken(jwtToken);
                } catch (SignatureException e) {
                    throw new CustomException(CustomException.INVALID_JWT_SIGNATURE, "Invalid JWT signature.");
                } catch (MalformedJwtException e) {
                    throw new CustomException(CustomException.INVALID_JWT_TOKEN, "Invalid JWT token.");
                } catch (ExpiredJwtException e) {
                    throw new CustomException(CustomException.EXPIRED_JWT_EXCEPTION, "JWT token is expired.");
                } catch (UnsupportedJwtException e) {
                    throw new CustomException(CustomException.UNSUPPORTED_JWT_EXCEPTION, "JWT token is unsupported.");
                } catch (IllegalArgumentException e) {
                    throw new CustomException(CustomException.INVALID_JWT_TOKEN, "JWT claims string is empty.");
                }
            }

            if (userName == null) {
                throw new CustomException(CustomException.INVALID_JWT_TOKEN, "Invalid JWT token.");
            }

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = null;
                try {
                    userDetails = userDetailsService.loadUserByUsername(userName);
                } catch (Exception e) {
                    throw new AuthenticationFailedException();
                }

                if (jwtUtil.validateToken(jwtToken, userDetails)) {

                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, userName, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                    throw new AuthenticationFailedException();
                }

            }

            User foundUser = userRepository.getById(userName);
            UserDto userDto = userService.convertUserToUserResponseDto(foundUser);

            return userDto;

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public boolean authorization(String username, String featureAction) throws CustomException {

        boolean authorization = false;
        try {

            int count = userRepository.getUserFeatureActionCount(username, featureAction);
            if (count > 0) {
                authorization = true;
            } else {
                throw new AuthorizationFailedException();

            }

            return authorization;

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public TokenRefreshResponseDto refreshToken(String requestTokenHeader, String refreshToken) throws CustomException {
        try {
            TokenRefreshResponseDto responseDto = new TokenRefreshResponseDto();
            String jwtToken;
            if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
                jwtToken = requestTokenHeader.substring(7);

            } else {
                jwtToken = requestTokenHeader;
            }
            if (jwtToken == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M07);
            }
            if (refreshToken == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M08);
            }

            User user = userRepository.findByToken(jwtToken);
            if (user == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M09);
            }
            if (!user.getRefreshToken().equals(refreshToken)) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M010);
            }

            final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEpfNumber());
            String temptoken = jwtUtil.generateToken(userDetails);
            refreshToken = UUID.randomUUID().toString();
            user.setToken(temptoken);
            user.setRefreshToken(refreshToken);
            user = userRepository.save(user);

            responseDto.setAccessToken(temptoken);
            responseDto.setRefreshToken(refreshToken);

            return responseDto;

        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

}
