package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.WorkflowStep;
import com.iterminal.ndis.model.WorkflowStepAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IWorkflowStepActionRepository extends JpaRepository<WorkflowStepAction, Long> {

    public List<WorkflowStepAction> findAllByWorkflowId(Long id);

    public List<WorkflowStepAction> findAllByWorkflowStepId(Long id);

}
