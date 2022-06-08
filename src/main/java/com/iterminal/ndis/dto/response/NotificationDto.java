package com.iterminal.ndis.dto.response;

import com.iterminal.ndis.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {

    private Long id;
    private String type;
    private String messageData;
    private String recipient;
    private String sender;
    private Boolean readMessage;
    private Long sendDateTime;
    private User senderUser;
    private User recipientUser;

}
