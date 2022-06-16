package com.iterminal.ndis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "workflow_step")
public class WorkflowStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workflow_step_id", nullable = false, unique = true, length = 50)
    private Long id;

    @Column(name = "workflow_id")
    private Long workflowId;

    @Column(name = "description")
    private String description;

    @Column(name = "step_type", nullable = false, length = 25)
    private String stepType;

    @Column(name = "step_assigned", nullable = false, length = 25)
    private String stepAssigned;

    @Column(name = "step_assigned_is_user", nullable = false, length = 25)
    private boolean stepAssignedIsUser;

    @Column(name = "step_order")
    private Integer stepOrder;

    @OneToMany
    @JoinColumn(name = "workflow_step_id", insertable = false, updatable = false)
    private List<WorkflowStepAction> stepActions = new ArrayList<>();
}
