package com.iterminal.ndis.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAndUserGroupsDto <T>{

    private String id;
    private String name;
    private boolean isUser;

}
