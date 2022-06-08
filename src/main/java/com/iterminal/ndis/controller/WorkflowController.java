package com.iterminal.ndis.controller;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.UserGroupRequestDto;
import com.iterminal.ndis.dto.WorkflowRequestDto;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.dto.response.UserGroupDto;
import com.iterminal.ndis.dto.response.WorkflowDto;
import com.iterminal.ndis.model.Company;
import com.iterminal.ndis.model.UserGroup;
import com.iterminal.ndis.model.Workflow;
import com.iterminal.ndis.service.IWorkflowService;
import com.iterminal.ndis.util.ErrorResponseUtil;
import com.iterminal.searchfilters.RequestListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("workflow-management/workflow")
public class WorkflowController {

    private final IWorkflowService workflowService;

    @Autowired
    WorkflowController(IWorkflowService workflowService){ this.workflowService = workflowService; }

    @GetMapping("/list/all")
    public ResponseEntity<List<WorkflowDto>> getWorkflows(){
        try {
            List<WorkflowDto> workflowList = workflowService.getAll();
            return new ResponseEntity<>(workflowList, HttpStatus.OK);
        } catch (CustomException ex){
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PostMapping(value = "/list/all")
    public ResponseEntity<PaginationDto<Workflow>> getAllWorkflows(@RequestBody RequestListDto requestList) {
        try {
            PaginationDto<Workflow> workflowList = workflowService.getPaginatedList(requestList);
            return new ResponseEntity<>(workflowList, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }


    @GetMapping("/list")
    public ResponseEntity<List<WorkflowDto>> getWorkflowByCreator(){
        try {
            List<WorkflowDto> workflowList = workflowService.getByCreator();
            return new ResponseEntity<>(workflowList, HttpStatus.OK);
        } catch (CustomException ex){
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<WorkflowDto> createWorkflow(@RequestBody WorkflowRequestDto workflowRequestDto){
        try{
            WorkflowDto savedWorkflow = workflowService.create(workflowRequestDto);
            return new ResponseEntity<>(savedWorkflow, HttpStatus.OK);
        } catch (CustomException ex){
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PutMapping(value = "/modify/{id}")
    public ResponseEntity<WorkflowDto> updateWorkflow(@PathVariable("id") long workflow_Id, @RequestBody WorkflowRequestDto workflow) {
        try {
            WorkflowDto updatedWorkflow = workflowService.update(workflow_Id, workflow);
            return new ResponseEntity<>(updatedWorkflow, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable("id") Long id) {

        try {
            workflowService.delete(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PutMapping(value = "/{id}/inactive")
    public ResponseEntity<Workflow> inactive(@PathVariable("id") long workflow_id) {

        try{
            Workflow inactivatedWorkflow  = workflowService.inactive(workflow_id);
            return new ResponseEntity<Workflow>(inactivatedWorkflow, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }
}
