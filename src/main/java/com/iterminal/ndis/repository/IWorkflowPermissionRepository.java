package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.TagPermission;
import com.iterminal.ndis.model.WorkflowPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IWorkflowPermissionRepository extends JpaRepository<WorkflowPermission, Long> {
    List<WorkflowPermission> findAllByWorkflowId(long workflow_id);

    @Query("SELECT g.workflowId from WorkflowPermission g where g.epfNumber=?1")
    List<Long> getWorkflowIdByUserId(String epfNo);
}
