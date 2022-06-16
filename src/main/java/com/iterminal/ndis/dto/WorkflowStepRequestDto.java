package com.iterminal.ndis.dto;

import com.iterminal.ndis.model.User;
import com.iterminal.ndis.model.WorkflowStepAction;

import java.util.ArrayList;
import java.util.List;

public class WorkflowStepRequestDto {
    private Long id;
    private Long workflowId;
    private String description;
    private String stepAssigned;
    private String stepType;
    private boolean stepAssignedIsUser;
    private Integer stepOrder;
    private List<WorkflowStepAction> stepActions = new ArrayList<>();
}
