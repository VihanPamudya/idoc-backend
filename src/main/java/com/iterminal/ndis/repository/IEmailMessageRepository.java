package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.EmailMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEmailMessageRepository extends JpaRepository<EmailMessage, Long> {
    public EmailMessage findEmailMessageByMessageValue (String value);
}
