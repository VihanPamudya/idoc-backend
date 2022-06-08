package com.iterminal.ndis.service;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.WorkflowRequestDto;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.dto.response.UserDto;
import com.iterminal.ndis.dto.response.WorkflowDto;
import com.iterminal.ndis.dto.response.WorkflowStepDto;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.model.UserGroup;
import com.iterminal.ndis.model.Workflow;
import com.iterminal.ndis.model.WorkflowStep;
import com.iterminal.searchfilters.RequestListDto;

import java.util.List;

public interface IWorkflowService {

    enum Status {
        Active,
        Inactive
    }

    public WorkflowDto create(WorkflowRequestDto workflowRequestDto) throws CustomException;

    public WorkflowDto update(long workflowId, WorkflowRequestDto workflowRequestDto) throws CustomException;

    public List<WorkflowDto> getAll() throws CustomException;

    public List<WorkflowDto> getByCreator() throws CustomException;

    public PaginationDto<Workflow> getPaginatedList(RequestListDto requestList) throws CustomException;

    public WorkflowStepDto convertWorkflowStepToWorkflowStepResponseDto(WorkflowStep workflowStep) throws CustomException;

    public WorkflowDto convertWorkflowToWorkflowResponseDto(Workflow workflow) throws CustomException;

    public void delete(Long id) throws CustomException;

    public Workflow inactive(long workflow_id) throws  CustomException;

    public List<Workflow> getWorkflowListByStatus(String status) throws CustomException;

}
