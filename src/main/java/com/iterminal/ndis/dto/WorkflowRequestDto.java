package com.iterminal.ndis.dto;

import com.iterminal.ndis.model.TagPermission;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.model.WorkflowPermission;
import com.iterminal.ndis.model.WorkflowStep;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowRequestDto {

    private Long workflowId;
    private String workflowName;
    private Long createdDateTime;
    private User createdBy;
    private List<WorkflowStep> steps = new ArrayList<>();
    private List<WorkflowPermission> permissionList = new ArrayList<>();

}
