package com.iterminal.ndis.service.impl;

import com.iterminal.exception.*;
import com.iterminal.ndis.dto.UserGroupRequestDto;
import com.iterminal.ndis.dto.WorkflowRequestDto;
import com.iterminal.ndis.dto.response.*;
import com.iterminal.ndis.model.*;
import com.iterminal.ndis.repository.*;
import com.iterminal.ndis.service.IUserGroupService;
import com.iterminal.ndis.service.IUserService;
import com.iterminal.ndis.service.IWorkflowService;
import com.iterminal.ndis.util.DataUtil;
import com.iterminal.ndis.util.InputValidatorUtil;
import com.iterminal.ndis.util.MessagesAndContent;
import com.iterminal.searchfilters.FilterOperator;
import com.iterminal.searchfilters.FilterUtil;
import com.iterminal.searchfilters.PropertyFilterDto;
import com.iterminal.searchfilters.RequestListDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.iterminal.ndis.util.MessagesAndContent.COMMON_M29;
import static com.iterminal.ndis.util.MessagesAndContent.GROUP_M05;

@Slf4j
@Service
public class WorkflowService implements IWorkflowService {

    private final IWorkflowRepository workflowRepository;
    private final CustomQueryRepository<Workflow> customQueryRepository;
    private final IWorkflowStepRepository workflowStepRepository;
    private final IWorkflowStepActionRepository workflowStepActionRepository;
    private final IWorkflowPermissionRepository workflowPermissionRepository;
    private final IUserRepository userRepository;
    private final IUserService userService;
    private final IUserGroupRepository userGroupRepository;

    final String WORKFLOW_STEP_ACTION_VALIDATE = "Validate";
    final String WORKFLOW_STEP_ACTION_ACCEPT = "Accept";
    final String WORKFLOW_STEP_ACTION_REJECT = "Reject";
    final String TABLE_USER = "workflow";
    final String USER_STATUS_ACTIVE = "Active";
    final String USER_STATUS_INACTIVE = "Inactive";


    final String WORKFLOW_STEP_SUB_ACTION_ADD_TAG = "Add";
    final String WORKFLOW_STEP_SUB_ACTION_REMOVE_TAG = "Remove";
    final String WORKFLOW_STEP_SUB_ACTION_PROCESS_FILES = "Process";


    enum WorkflowStepSubActionTypes {
        Add,
        Remove,
        Process
    }

    @Autowired
    public WorkflowService(
            IWorkflowRepository workflowRepository,
            IUserRepository userRepository,
            IUserService userService,
            IWorkflowStepRepository workflowStepRepository,
            IWorkflowStepActionRepository workflowStepActionRepository,
            IWorkflowPermissionRepository workflowPermissionRepository,
             CustomQueryRepository customQueryRepository,
            IUserGroupRepository userGroupRepository
    ){
        this.workflowRepository = workflowRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.workflowStepRepository = workflowStepRepository;
        this.workflowStepActionRepository = workflowStepActionRepository;
        this.workflowPermissionRepository = workflowPermissionRepository;
        this.customQueryRepository = customQueryRepository;
        this.userGroupRepository = userGroupRepository;
    }

    @Override
    public WorkflowDto create(WorkflowRequestDto workflowRequestDto) throws CustomException {

        try{

            Workflow workflow = new Workflow();

            if(workflowRequestDto == null) {
                throw new InvalidInputException("Invalid Input 1");
            }

            long currentTime = Instant.now().getEpochSecond();

            String workflowName = InputValidatorUtil.validateStringProperty("Invalid Workflow Name", workflowRequestDto.getWorkflowName(), "Workflow Name", 100);
            Optional<Workflow> foundWorkflow = workflowRepository.findByName(workflowName);
            if(foundWorkflow.isPresent()) {
                throw new AlreadyExistException("A Workflow with name " + workflowName + " already exists.");
            }
            workflow.setWorkflowName(workflowName);

            User user = userRepository.getById(DataUtil.getUserName());
            workflow.setCreatedBy(user);

            workflow.setCreatedDateTime(currentTime);
            workflow.setStatus(USER_STATUS_ACTIVE);

            List<WorkflowStep> workflowStepList = new ArrayList<>();
            workflow.setSteps(workflowStepList);

            Workflow savedWorkflow = workflowRepository.save(workflow);
            if(!workflowRequestDto.getSteps().isEmpty()){
                for(int i=0; i<workflowRequestDto.getSteps().size(); i++){
                    WorkflowStep workflowStep = (WorkflowStep) workflowRequestDto.getSteps().toArray()[i];

                    WorkflowStep savingWorkflowStep = new WorkflowStep();

                    savingWorkflowStep.setWorkflowId(savedWorkflow.getId());

                    String description = InputValidatorUtil.validateStringProperty("Invalid Workflow Step Description", workflowStep.getDescription(), "Workflow Step Description", 225);
                    savingWorkflowStep.setDescription(description);

                    String stepType = InputValidatorUtil.validateStringProperty("Invalid Workflow Step Type", workflowStep.getStepType(), "Workflow Step Type", 25);
                    savingWorkflowStep.setStepType(stepType);

                    savingWorkflowStep.setStepOrder(i+1);

                    Optional<User> foundUser = userRepository.findById(workflowStep.getStepAssigned().getEpfNumber());
                    if(!foundUser.isPresent()) {
                        throw new DoesNotExistException("Step Assigned User Does not exist");
                    } else {
                        savingWorkflowStep.setStepAssigned(foundUser.get());
                    }

                    List<WorkflowStepAction> workflowStepActionList = new ArrayList<>();
                    savingWorkflowStep.setStepActions(workflowStepActionList);

                    WorkflowStep savedWorkflowStep = workflowStepRepository.save(savingWorkflowStep);

                    for(WorkflowStepAction workflowStepAction: workflowStep.getStepActions()){
                        WorkflowStepAction savedWorkflowStepAction = new WorkflowStepAction();

                        savedWorkflowStepAction.setWorkflowStepId(savedWorkflowStep.getId());

                        switch (workflowStepAction.getWorkflowStepAction()){
                            case WORKFLOW_STEP_ACTION_ACCEPT:
                                savedWorkflowStepAction.setWorkflowStepAction(WORKFLOW_STEP_ACTION_ACCEPT);
                                break;
                            case WORKFLOW_STEP_ACTION_REJECT:
                                savedWorkflowStepAction.setWorkflowStepAction(WORKFLOW_STEP_ACTION_REJECT);
                                break;
                            case WORKFLOW_STEP_ACTION_VALIDATE:
                                savedWorkflowStepAction.setWorkflowStepAction(WORKFLOW_STEP_ACTION_VALIDATE);
                                break;
                        }

                        switch (workflowStepAction.getWorkflowStepSubAction()){
                            case WORKFLOW_STEP_SUB_ACTION_ADD_TAG:
                                savedWorkflowStepAction.setWorkflowStepSubAction(WORKFLOW_STEP_SUB_ACTION_ADD_TAG);
                                savedWorkflowStepAction.setTagName(workflowStepAction.getTagName());
                                break;
                            case WORKFLOW_STEP_SUB_ACTION_REMOVE_TAG:
                                savedWorkflowStepAction.setWorkflowStepSubAction(WORKFLOW_STEP_SUB_ACTION_REMOVE_TAG);
                                savedWorkflowStepAction.setTagName(workflowStepAction.getTagName());
                                break;
                            case WORKFLOW_STEP_SUB_ACTION_PROCESS_FILES:
                                savedWorkflowStepAction.setWorkflowStepSubAction(WORKFLOW_STEP_SUB_ACTION_PROCESS_FILES);
                                break;
                        }

                        workflowStepActionList.add(savedWorkflowStepAction);
                    }

                    workflowStepActionRepository.saveAll(workflowStepActionList);
                    workflowStepList.add(savingWorkflowStep);
                }
            }



            savedWorkflow.setSteps(workflowStepList);

            workflowStepRepository.saveAll(workflowStepList);
            workflowRepository.save(savedWorkflow);


            List<WorkflowPermission> savePermissionList = new ArrayList<>();
            for (WorkflowPermission permission : workflowRequestDto.getPermissionList()) {

                if(!permission.getEpfNumber().isEmpty()) {

                    String epfNumber = permission.getEpfNumber();
                    Optional<User> foundUsers = userRepository.findById(epfNumber);
                    if (!foundUsers.isPresent()) {
                        throw new DoesNotExistException(MessagesAndContent.USER_M09);
                    }

                } else {
                    Long group_id = permission.getGroup_id();
                    Optional<UserGroup> foundUserGroup = userGroupRepository.findById(group_id);
                    if (!foundUserGroup.isPresent()) {
                        throw new DoesNotExistException(MessagesAndContent.GROUP_M03);
                    }
                }
                permission.setWorkflow_id(savedWorkflow.getId());
                WorkflowPermission workflowPermission = workflowPermissionRepository.save(permission);
                savePermissionList.add(workflowPermission);
            }

            WorkflowDto workflowDto = convertWorkflowToWorkflowResponseDto(savedWorkflow);
            return workflowDto;

        } catch (CustomException ex){
            throw ex;
        }
    }

    @Override
    public WorkflowDto update(long workflow_Id, WorkflowRequestDto workflowRequestDto) throws CustomException {
        try {
            if(workflowRequestDto == null ) {
                throw new InvalidInputException("Invalid Input");
            }

            Optional<Workflow> foundWorkflow = workflowRepository.findById(workflow_Id);


            if(foundWorkflow.isEmpty()) {
                throw new DoesNotExistException("Workflow Does Not exist");
            }

            String userId = DataUtil.getUserName();
            if(!foundWorkflow.get().getCreatedBy().getEpfNumber().equals(userId)) {
                throw new CustomException(MessagesAndContent.COMMON_M27);
            }

            Workflow newWorkflow = new Workflow();

            newWorkflow.setId(workflow_Id);
            newWorkflow.setCreatedBy(foundWorkflow.get().getCreatedBy());
            newWorkflow.setCreatedDateTime(foundWorkflow.get().getCreatedDateTime());
            newWorkflow.setStatus(foundWorkflow.get().getStatus());

            String workflowName = InputValidatorUtil.validateStringProperty(MessagesAndContent.WORKFLOW_M02, workflowRequestDto.getWorkflowName(), "Workflow Name", 50);
            Optional<Workflow> workflowFound = workflowRepository.findByName(workflowName);
            if(workflowFound.isPresent() && !workflowName.equalsIgnoreCase(workflowRequestDto.getWorkflowName())) {
                throw new AlreadyExistException("A Workflow with name " + workflowName + " already exists.");
            }
            newWorkflow.setWorkflowName(workflowName);

            if(!foundWorkflow.get().getSteps().isEmpty()) {
//                workflowStepRepository.deleteAllInBatch(foundWorkflow.get().getSteps());
            }

            List<WorkflowStep> workflowStepList = new ArrayList<>();

            for(int i=0; i<workflowRequestDto.getSteps().size(); i++){
                WorkflowStep workflowStep = (WorkflowStep) workflowRequestDto.getSteps().toArray()[i];

                WorkflowStep newWorkflowStep = new WorkflowStep();
                WorkflowStep currentWorkflowStep = null;
                Long workflowStepId = null;

                if(workflowStep.getId() != null){
                    currentWorkflowStep = foundWorkflow.get().getSteps().stream().filter(workflowStep1 -> Objects.equals(workflowStep1.getId(), workflowStep.getId())).toArray().length > 0 ? (WorkflowStep) foundWorkflow.get().getSteps().stream().filter(workflowStep1 -> Objects.equals(workflowStep1.getId(), workflowStep.getId())).toArray()[0] : null;

                    if(currentWorkflowStep == null){
                        throw new DoesNotExistException("Workflow Step with id "  +workflowStep.getId()+ " does not exist");
                    }
                    workflowStepId = currentWorkflowStep.getId();
                }

                newWorkflowStep.setId(workflowStepId);
                newWorkflowStep.setWorkflowId(workflow_Id);

                String workflowDescription = InputValidatorUtil.validateStringProperty(MessagesAndContent.WORKFLOW_M03, workflowStep.getDescription(), "Workflow Description", 225);
                newWorkflowStep.setDescription(workflowDescription);

                String workflowStepType = InputValidatorUtil.validateStringProperty(MessagesAndContent.WORKFLOW_M04, workflowStep.getStepType(), "Workflow Step Type", 25);
                newWorkflowStep.setStepType(workflowStepType);

                newWorkflowStep.setStepOrder(i+1);

                Optional<User> foundUser = userRepository.findById(workflowStep.getStepAssigned().getEpfNumber());
                if(foundUser.isEmpty()) {
                    throw new DoesNotExistException("Step Assigned User Does not exist");
                } else {
                    newWorkflowStep.setStepAssigned(foundUser.get());
                }

                List<WorkflowStepAction> workflowStepActionList = new ArrayList<>();
                newWorkflowStep.setStepActions(workflowStepActionList);

                if(workflowStepId != null){
                    WorkflowStep savedWorkflowStep = workflowStepRepository.save(newWorkflowStep);

                    workflowStepId = savedWorkflowStep.getId();
                }
                for(WorkflowStepAction workflowStepAction: workflowStep.getStepActions()){

                    WorkflowStepAction newWorkflowStepAction = new WorkflowStepAction();
                    Long workflowStepActionId = null;

                    if(workflowStepAction.getId() != null){
                        Optional<WorkflowStepAction> savedWorkflowStepAction = workflowStepActionRepository.findById(workflowStepAction.getId());

                        if(savedWorkflowStepAction.isEmpty()){
                            throw new DoesNotExistException("Workflow Step Action with id "  +workflowStepAction.getId()+ " does not exist");
                        }
                        workflowStepActionId = savedWorkflowStepAction.get().getId();
                    }

                    newWorkflowStepAction.setWorkflowId(workflow_Id);
                    newWorkflowStepAction.setWorkflowStepId(workflowStepId);
                    newWorkflowStepAction.setId(workflowStepActionId);

                    switch (workflowStepAction.getWorkflowStepAction()){
                        case WORKFLOW_STEP_ACTION_ACCEPT:
                            newWorkflowStepAction.setWorkflowStepAction(WORKFLOW_STEP_ACTION_ACCEPT);
                            break;
                        case WORKFLOW_STEP_ACTION_REJECT:
                            newWorkflowStepAction.setWorkflowStepAction(WORKFLOW_STEP_ACTION_REJECT);
                            break;
                        case WORKFLOW_STEP_ACTION_VALIDATE:
                            newWorkflowStepAction.setWorkflowStepAction(WORKFLOW_STEP_ACTION_VALIDATE);
                            break;
                    }

                    switch (workflowStepAction.getWorkflowStepSubAction()){
                        case WORKFLOW_STEP_SUB_ACTION_ADD_TAG:
                            newWorkflowStepAction.setWorkflowStepSubAction(WORKFLOW_STEP_SUB_ACTION_ADD_TAG);
                            String tagName = InputValidatorUtil.validateStringProperty(MessagesAndContent.WORKFLOW_M05, workflowStepAction.getTagName(), "Workflow Step Action Tag Name", 25);
                            newWorkflowStepAction.setTagName(tagName);
                            break;
                        case WORKFLOW_STEP_SUB_ACTION_REMOVE_TAG:
                            newWorkflowStepAction.setWorkflowStepSubAction(WORKFLOW_STEP_SUB_ACTION_REMOVE_TAG);
                            String tagNameRemove = InputValidatorUtil.validateStringProperty(MessagesAndContent.WORKFLOW_M05, workflowStepAction.getTagName(), "Workflow Step Action Tag Name", 25);
                            newWorkflowStepAction.setTagName(tagNameRemove);
                            break;
                        case WORKFLOW_STEP_SUB_ACTION_PROCESS_FILES:
                            newWorkflowStepAction.setWorkflowStepSubAction(WORKFLOW_STEP_SUB_ACTION_PROCESS_FILES);
                            break;
                    }

                    workflowStepActionList.add(newWorkflowStepAction);
                }

                workflowStepActionRepository.deleteAllInBatch(workflowStepActionRepository.findAllByWorkflowStepId(workflowStepId));
                System.out.println(workflowStepActionList);
                workflowStepActionRepository.saveAll(workflowStepActionList);

                newWorkflowStep.setStepActions(workflowStepActionList);
                workflowStepList.add(newWorkflowStep);

            }

            newWorkflow.setSteps(workflowStepList);

            workflowStepRepository.saveAll(workflowStepList);
            workflowRepository.save(newWorkflow);

            WorkflowDto workflowDto = convertWorkflowToWorkflowResponseDto(newWorkflow);
            return workflowDto;

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public List<WorkflowDto> getByCreator() throws CustomException{

        try {
            List<Workflow> workflowList = workflowRepository.findAllByCreatedBy_EpfNumber(DataUtil.getUserName());

            List<WorkflowDto> workflowDtoList = new ArrayList<>();
            for(Workflow workflow: workflowList){
                workflowDtoList.add(convertWorkflowToWorkflowResponseDto(workflow));
            }
            return workflowDtoList;

        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }

    }

    @Override
    public List<WorkflowDto> getAll() throws CustomException{

        try {
            List<Workflow> workflowList = workflowRepository.findAll();

            List<WorkflowDto> workflowDtoList = new ArrayList<>();
            for(Workflow workflow: workflowList){
                workflowDtoList.add(convertWorkflowToWorkflowResponseDto(workflow));
            }
            return workflowDtoList;

        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }

    }

    @Override
    public PaginationDto<Workflow> getPaginatedList(RequestListDto requestList) throws CustomException{
        try {

            if (requestList == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }
            PaginationDto<Workflow> paginationDto = new PaginationDto<>();
            List<Workflow> list = new ArrayList<>();
            int page = requestList.getPage();
            int limit = requestList.getLimit();
            if (page <= 0) {
                page = 1;
            }
            if (limit <= 0) {
                limit = 20;
            }

            List<PropertyFilterDto> propertyFilterDtoList = requestList.getFilterData();
            PropertyFilterDto InactivePFD = new PropertyFilterDto();



            InactivePFD.setValue("'Active'");
            InactivePFD.setProperty("status");
            InactivePFD.setOperator(FilterOperator.EQUAL);

            propertyFilterDtoList.add(InactivePFD);

            String sql = FilterUtil.generateListSql(TABLE_USER,  propertyFilterDtoList, requestList.getOrderFields(), true, page, limit);
            System.out.println(sql);
            System.out.println(requestList.getPage());
            list = customQueryRepository.getResultList(sql, Workflow.class);
            paginationDto.setData(list);
            paginationDto.setTotalSize(workflowRepository.countWorkflowsByStatusEquals("Active"));
            return  paginationDto;
        } catch (CustomException ex) {
            //log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            String err = "";
            if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause().getMessage() != null) {
                err = ex.getCause().getCause().getMessage();
            }
            //log.error(ex.getMessage());
            throw new InvalidFilterInputException("Sql query execute failed. " + err);
        }
    }

    @Override
    public Workflow inactive(long workflow_id) throws CustomException {
        try {
            Optional<Workflow> foundWorkflow = workflowRepository.findById(workflow_id);
            if (!foundWorkflow.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.WORKFLOW_M01);
            }
            Workflow currentWorkflow = foundWorkflow.get();
            if (currentWorkflow.getStatus().equals(USER_STATUS_INACTIVE)) {
                throw new AlreadyExistException(MessagesAndContent.WORKFLOW_M07);
            }

            currentWorkflow.setStatus(USER_STATUS_INACTIVE);
            Workflow updatedWorkflow = workflowRepository.save(currentWorkflow);

            return updatedWorkflow;

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    public void delete(Long id) throws CustomException {

        try {

            Optional<Workflow> foundWorkflow = workflowRepository.findById(id);

            if (foundWorkflow.isPresent()) {
                String userId = DataUtil.getUserName();
                if(foundWorkflow.get().getCreatedBy().getEpfNumber().equals(userId)) {
                    workflowRepository.delete(foundWorkflow.get());

                    List<WorkflowStep> foundWorkflowSteps = workflowStepRepository.findAllByWorkflowId(id);
                    workflowStepRepository.deleteAll(foundWorkflowSteps);

                    List<WorkflowStepAction> foundWorkflowStepActions = workflowStepActionRepository.findAllByWorkflowId(id);
                    workflowStepActionRepository.deleteAll(foundWorkflowStepActions);
                } else {
                    throw new CustomException(MessagesAndContent.COMMON_M27);
                }
            } else {
                throw new DoesNotExistException(MessagesAndContent.WORKFLOW_M01);
            }

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }

    }

    @Override
    public WorkflowStepDto convertWorkflowStepToWorkflowStepResponseDto(WorkflowStep workflowStep) throws CustomException{
        WorkflowStepDto workflowStepDto = new WorkflowStepDto();
        BeanUtils.copyProperties(workflowStep, workflowStepDto);

        workflowStepDto.setStepAssigned(userService.convertUserToUserBasicResponseDto(workflowStep.getStepAssigned()));

        return workflowStepDto;
    }

    @Override
    public WorkflowDto convertWorkflowToWorkflowResponseDto(Workflow workflow) throws CustomException{
        WorkflowDto workflowDto = new WorkflowDto();
        BeanUtils.copyProperties(workflow, workflowDto);

        UserBasicDto userBasicDto = userService.convertUserToUserBasicResponseDto(workflow.getCreatedBy());

        workflowDto.setCreatedBy(userBasicDto);

        List<WorkflowStepDto> workflowStepDtoList = new ArrayList<>();
        for(WorkflowStep workflowStep: workflow.getSteps()){
            workflowStepDtoList.add(convertWorkflowStepToWorkflowStepResponseDto(workflowStep));
        }
        workflowDto.setSteps(workflowStepDtoList);

        return workflowDto;
    }

    @Override
    public List<Workflow> getWorkflowListByStatus(String status) throws CustomException {
        try {
            IWorkflowService.Status statusValue = null;

            String validatedStatus = InputValidatorUtil.validateStringProperty(GROUP_M05, status);

            try {
                statusValue = IWorkflowService.Status.valueOf(validatedStatus);
            } catch (Exception e) {
                throw new InvalidInputException(COMMON_M29);
            }

            List<Workflow> foundWorkflowList = workflowRepository.findAllByStatus(validatedStatus);

            return foundWorkflowList;
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception e) {
            throw new UnknownException(e.getMessage());
        }
    }
}
