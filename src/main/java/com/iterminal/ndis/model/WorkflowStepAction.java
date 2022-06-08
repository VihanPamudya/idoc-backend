package com.iterminal.ndis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "workflow_step_action")
public class WorkflowStepAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workflow_step_action_id", nullable = false, unique = true, length = 50)
    private Long id;

    @Column(name = "workflow_id")
    private Long workflowId;

    @Column(name = "workflow_step_id")
    private Long workflowStepId;

    @Column(name = "workflow_step_action", length = 10)
    private String workflowStepAction;

    @Column(name = "workflow_step_sub_action", length = 20)
    private String workflowStepSubAction;

    @Column(name = "tag_name", length = 50)
    private String tagName;
}
