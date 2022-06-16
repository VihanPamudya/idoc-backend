
package com.iterminal.ndis.model;

import lombok.Data;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "message_data", length = 100)
    private String messageData;

    @Column(name = "recipient", length = 50)
    private String recipient;

    @Column(name = "sender", length = 50)
    private String sender;

    @Column(name = "read_message")
    private Boolean readMessage;

    @Column(name = "send_date_time")
    private Long sendDateTime;

    @ManyToOne
    @JoinColumn(name = "sender", insertable = false, updatable = false)
    private User senderUser;

    @ManyToOne
    @JoinColumn(name = "recipient", insertable = false, updatable = false)
    private User recipientUser;
}
