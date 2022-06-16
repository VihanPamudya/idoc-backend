package com.iterminal.ndis.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "email_messages")
public class EmailMessage {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "email_message_id", unique = true, nullable = false, length = 50)
    private Long id;

    @Column(name = "message_value", length = 50, updatable = false )
    private String messageValue;

    @Column(name = "message_text", columnDefinition = "TEXT" )
    private String messageText;
}
