package com.iterminal.ndis.controller;

import com.iterminal.ndis.dto.CountResultDto;
import com.iterminal.ndis.dto.UserRequestDto;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.dto.response.UserDto;
import com.iterminal.exception.CustomException;
import com.iterminal.ndis.model.Company;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.model.UserHistory;
import com.iterminal.ndis.util.ErrorResponseUtil;
import com.iterminal.searchfilters.RequestListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import com.iterminal.ndis.service.IUserService;
import com.iterminal.searchfilters.RequestListCountDto;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/user-management/user")
public class UserController {

    private final IUserService userService;

    @Autowired
    public UserController(IUserService userService) {
        this.userService = userService;
    }

    //Find user by user name
    @GetMapping(value = "/view/{name}")
    public ResponseEntity<UserDto> getUser(@PathVariable("name") String epfNumber) {

        try {
            UserDto userDto = userService.findUserById(epfNumber);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    //Get user list
    @GetMapping(value = "/list")
    public ResponseEntity<List<User>> getAllUsers() {

        try {
            List<User> userList = userService.getAll();
            return new ResponseEntity<>(userList, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    //Get user count
    @GetMapping(value = "/count")
    public ResponseEntity<CountResultDto> getCount() {

        try {
            CountResultDto countDro = new CountResultDto();
            long count = userService.getCount(null);
            countDro.setCount(count);
            return new ResponseEntity<>(countDro, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    //Get paginated list
    @PostMapping(value = "/list")
    public ResponseEntity<PaginationDto<User>> getUsers(@RequestBody RequestListDto requestList) {
        try {
            PaginationDto<User> userList = userService.getPaginatedList(requestList);
            return new ResponseEntity<>(userList, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PostMapping(value = "/count")
    public ResponseEntity<CountResultDto> getCount(@RequestBody RequestListCountDto requestListCount) {

        try {
            CountResultDto countDro = new CountResultDto();
            long count = userService.getCount(requestListCount);
            countDro.setCount(count);
            return new ResponseEntity<>(countDro, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PostMapping(value = "/add")
    public ResponseEntity<UserDto> createUser(@RequestBody UserRequestDto user) {

        try {
            UserDto savedUser = userService.create(user);

            return new ResponseEntity<>(savedUser, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PutMapping(value = "/modify/{name}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("name") String epfNumber, @RequestBody UserRequestDto user) {

        try {
            UserDto updatedUser = userService.update(epfNumber, user);

            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @DeleteMapping(value = "/delete/{name}")
    public ResponseEntity<Void> deleteUser(@PathVariable("name") List<String> epfNumber) {

        try {
            for (String id : epfNumber) {
                userService.delete(id);
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PutMapping(value = "/{name}/active")
    public ResponseEntity<User> active(@PathVariable("name") String epfNumber) {

        try {
            User updatedUser = userService.active(epfNumber);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PutMapping(value = "/{name}/inactive")
    public ResponseEntity<List<User>> inactive(@PathVariable("name") String epfNumber) {

        try {
            List<User> listUser = new ArrayList<>();
            User updatedUser = userService.inactive(epfNumber);
            listUser.add(updatedUser);
            return new ResponseEntity<List<User>>(listUser, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }


    @GetMapping(value = "/{name}/history")
    public ResponseEntity<List<UserHistory>> getUserHistory(@PathVariable("name") String epfNumber) {

        try {
            List<UserHistory> userHistory = userService.getUserHistory(epfNumber);
            return new ResponseEntity<>(userHistory, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }
}
