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
@Table(name = "workflow")
public class Workflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "workflow_id", unique = true, nullable = false, length = 50)
    private Long id;

    @Column(name = "workflow_name", length = 50, nullable = false)
    private String workflowName;

    @Column(name = "created_date_time", nullable = false)
    private Long createdDateTime;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @ManyToOne
    @JoinColumn(name = "createdBy", referencedColumnName = "epf_number")
    private User createdBy;

    @OneToMany
    @JoinColumn(name = "workflow_id", insertable = false, updatable = false)
    private List<WorkflowStep> steps = new ArrayList<>();
    
    @OneToMany
    @JoinColumn(name = "workflow_id", insertable = false, updatable = false)
    private List<WorkflowPermission> permissions = new ArrayList<>();

}
