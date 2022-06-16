package com.iterminal.ndis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBasicDto {

    private String epfNumber;
    private String userName;
    private String firstName;
    private String lastName;

}
