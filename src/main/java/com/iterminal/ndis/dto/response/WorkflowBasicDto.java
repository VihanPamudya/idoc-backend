package com.iterminal.ndis.dto.response;

import com.iterminal.ndis.model.WorkflowStep;
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
public class WorkflowBasicDto {
    private Long id;
    private String workflowName;
    private List<WorkflowStep> steps = new ArrayList<>();
}
