package com.iterminal.ndis.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserGroupDto {
    private Long id;
    private Long parentGroup_id;
    private String name;
    private Long createdDateTime;
    private String createdBy;
    private int member_count;
    private String parent_group_name;
}