package com.iterminal.ndis.controller;


import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.response.UserAndUserGroupsDto;
import com.iterminal.ndis.model.EmailMessage;
import com.iterminal.ndis.service.IEmailMessageService;
import com.iterminal.ndis.service.ISearchService;
import com.iterminal.ndis.util.ErrorResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("system-configuration/email-management/")
public class EmailMessageController {
    private final IEmailMessageService emailMessageService;

    @Autowired
    public EmailMessageController(IEmailMessageService emailMessageService){
        this.emailMessageService = emailMessageService;
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<EmailMessage>> getEmailMessages (){

        try{
            List<EmailMessage> emailMessageList = emailMessageService.getAll();
            return new ResponseEntity<>(emailMessageList, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }


    @PutMapping(value = "/modify/{id}")
    public ResponseEntity<EmailMessage> updateEmailMessage (@PathVariable("id") Long id, EmailMessage emailMessageRequest){

        try{
            EmailMessage emailMessage = emailMessageService.update(id, emailMessageRequest);
            return new ResponseEntity<>(emailMessage, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }
}
