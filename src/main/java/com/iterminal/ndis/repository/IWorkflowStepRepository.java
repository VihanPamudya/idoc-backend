package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.WorkflowStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IWorkflowStepRepository extends JpaRepository<WorkflowStep, Long> {

    public List<WorkflowStep> findAllByWorkflowId(Long id);

}
