package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.TagPermission;
import com.iterminal.ndis.model.WorkflowPermission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWorkflowPermissionRepository extends JpaRepository<WorkflowPermission, String> {
    WorkflowPermission findById(long workflow_id);
}
