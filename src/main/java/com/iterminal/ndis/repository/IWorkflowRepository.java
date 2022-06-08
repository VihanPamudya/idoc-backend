package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.UserGroup;
import com.iterminal.ndis.model.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IWorkflowRepository extends JpaRepository<Workflow, Long> {

    public List<Workflow> findAllByCreatedBy_EpfNumber(String epf_number);

    List<Workflow> findAllByStatus(String status);

    @Query("SELECT g from Workflow g where g.workflowName=?1")
    Optional<Workflow> findByName(String name);

    int countWorkflowsByStatusEquals(String status);
}
