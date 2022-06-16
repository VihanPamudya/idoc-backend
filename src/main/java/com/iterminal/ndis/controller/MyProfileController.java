package com.iterminal.ndis.controller;

import com.iterminal.ndis.dto.CountResultDto;
import com.iterminal.ndis.dto.RequestChanagePasswordDto;
import com.iterminal.ndis.dto.response.NotificationDto;
import com.iterminal.ndis.dto.response.UserDto;
import com.iterminal.exception.CustomException;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.service.IUserService;
import com.iterminal.ndis.util.DataUtil;
import com.iterminal.ndis.util.ErrorResponseUtil;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/my-profile")
public class MyProfileController {

    private final IUserService userService;

    @Autowired
    public MyProfileController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/view")
    public ResponseEntity<UserDto> getUser() {

        try {

            String epfNumber = DataUtil.getUserName();

            UserDto user = userService.findUserById(epfNumber);

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping(value = "/modify/{name}")
    public ResponseEntity<UserDto> updateUserProfile(@PathVariable("name") String epfNumber, @RequestBody User user) {

        try {

            String userName = DataUtil.getUserName();

            if (!epfNumber.equals(userName)) {
                throw new CustomException(CustomException.INVALID_JWT_TOKEN, "Invalid JWT token.");
            }

            UserDto updatedUser = userService.updateUserProfile(epfNumber, user);

            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PostMapping(value = "/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody RequestChanagePasswordDto request) {

        try {

            String userName = DataUtil.getUserName();

            if (!userName.equals(request.getUsername())) {
                throw new CustomException(CustomException.INVALID_JWT_TOKEN, "Invalid JWT token.");
            }

            userService.changePassword(request);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PostMapping(value = "/reset-password")
    public ResponseEntity<Void> resetPassword() {

        try {

            String epfNumber = DataUtil.getUserName();

            userService.resetPassword(epfNumber);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @GetMapping(value = "/view/notification/list")
    public ResponseEntity<List<NotificationDto>> getNotificationList() {
        try {

            List<NotificationDto> notifications = userService.getNotificationList();

            return new ResponseEntity<>(notifications, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @GetMapping(value = "/view/notification/count")
    public ResponseEntity<CountResultDto> getNotificationCount() {

        try {
            CountResultDto countDro = new CountResultDto();
            long count = userService.getNotificationCount();
            countDro.setCount(count);
            return new ResponseEntity<>(countDro, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PutMapping(value = "/view/notification/{notification_id}/read")
    public ResponseEntity<Void> readNotification(@PathVariable("notification_id") Long notificationId) {
        try {
            userService.readNotification(notificationId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }
}
