package com.iterminal.ndis.service;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.model.EmailMessage;

import java.util.List;

public interface IEmailMessageService {
    EmailMessage update(Long id, EmailMessage emailMessage) throws CustomException;

    List<EmailMessage> getAll() throws CustomException;
}
