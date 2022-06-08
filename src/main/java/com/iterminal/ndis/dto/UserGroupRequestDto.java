package com.iterminal.ndis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroupRequestDto {
    private  Long group_id;
    private Long parentGroup_id;
    private String name;
    private Long createdDateTime;
    private String createdBy;
}

