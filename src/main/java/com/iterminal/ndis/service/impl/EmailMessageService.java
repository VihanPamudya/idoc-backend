package com.iterminal.ndis.service.impl;

import com.iterminal.exception.*;
import com.iterminal.ndis.model.*;
import com.iterminal.ndis.repository.IEmailMessageRepository;
import com.iterminal.ndis.service.IEmailMessageService;
import com.iterminal.ndis.util.InputValidatorUtil;
import com.iterminal.ndis.util.MessagesAndContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EmailMessageService implements IEmailMessageService {

    private final IEmailMessageRepository emailMessageRepository;

    public EmailMessageService(IEmailMessageRepository emailMessageRepository){
        this.emailMessageRepository = emailMessageRepository;
    }


    @Override
    public EmailMessage update(Long id, EmailMessage emailMessage) throws CustomException{

        try {

            if (emailMessage == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }

            Optional<EmailMessage> foundEmailMessage = emailMessageRepository.findById(id);

            if (foundEmailMessage.isEmpty()) {
                throw new DoesNotExistException(MessagesAndContent.USER_M09);
            }

            EmailMessage newEmailMessage = foundEmailMessage.get();

            String message = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, emailMessage.getMessageText());
            newEmailMessage.setMessageText(message);

            emailMessageRepository.save(newEmailMessage);

            return newEmailMessage;

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }


    @Override
    public List<EmailMessage> getAll() throws CustomException {
        try {
            return emailMessageRepository.findAll();

        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }
}
