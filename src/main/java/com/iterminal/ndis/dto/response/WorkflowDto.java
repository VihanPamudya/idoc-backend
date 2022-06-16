package com.iterminal.ndis.dto.response;


import com.iterminal.ndis.dto.WorkflowPermissionRequestDto;
import com.iterminal.ndis.model.WorkflowPermission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowDto {
    private Long id;
    private String workflowName;
    private Long createdDateTime;
    private UserBasicDto createdBy;
    private List<WorkflowStepDto> steps = new ArrayList<>();
    private List<WorkflowPermissionRequestDto> permissionList = new ArrayList<>();
}
