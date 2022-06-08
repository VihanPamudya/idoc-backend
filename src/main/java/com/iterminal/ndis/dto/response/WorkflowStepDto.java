package com.iterminal.ndis.dto.response;

import com.iterminal.ndis.model.User;
import com.iterminal.ndis.model.WorkflowStepAction;
import io.swagger.models.auth.In;
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
public class WorkflowStepDto {
    private Long id;
    private Long workflowId;
    private String description;
    private String stepType;
    private UserBasicDto stepAssigned;
    private Integer stepOrder;
    private List<WorkflowStepAction> stepActions = new ArrayList<>();

}
