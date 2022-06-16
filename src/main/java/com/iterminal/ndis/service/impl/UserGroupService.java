package com.iterminal.ndis.service.impl;

import com.iterminal.exception.*;
import com.iterminal.ndis.dto.UserGroupRequestDto;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.dto.response.UserGroupDto;
import com.iterminal.ndis.model.*;
import com.iterminal.ndis.repository.*;
import com.iterminal.ndis.service.IUserGroupService;
import com.iterminal.ndis.service.IUserService;
import com.iterminal.ndis.util.DataUtil;
import com.iterminal.ndis.util.InputValidatorUtil;
import com.iterminal.ndis.util.MessagesAndContent;
import com.iterminal.searchfilters.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.iterminal.ndis.util.MessagesAndContent.*;

@Service
public class UserGroupService implements IUserGroupService {
    private final IUserGroupRepository userGroupRepository;
    private final IUserGroupGroupsRepository userGroupGroupsRepository;
    private final IUserRepository userRepository;
    private final CustomQueryRepository<UserGroup> customQueryRepository;

    final String TABLE_USER_GROUP = "user_group";
    final String USER_STATUS_ACTIVE = "Active";
    final String USER_STATUS_INACTIVE = "Inactive";


    @Autowired
    public UserGroupService(IUserGroupRepository userGroupRepository,
                            IUserGroupGroupsRepository userGroupGroupsRepository,
                            IUserRepository userRepository,
                            CustomQueryRepository customQueryRepository) {
        this.userGroupRepository = userGroupRepository;
        this.userGroupGroupsRepository = userGroupGroupsRepository;
        this.userRepository = userRepository;
        this.customQueryRepository = customQueryRepository;
    }

    @Override
    public UserGroupDto create(UserGroupRequestDto request) throws CustomException {
        try {
            UserGroup newGroupRequest = new UserGroup();

            if(request == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }

            String name = InputValidatorUtil.validateStringProperty(MessagesAndContent.GROUP_M01, request.getName(), "Group Name", 50);

            Optional<UserGroup> foundGroup = userGroupRepository.findByName(name);
            if(foundGroup.isPresent()) {
                throw new AlreadyExistException("A User Group with name " + name + " already exists.");
            }
            newGroupRequest.setName(name);

            long parentGroup_Id = request.getParentGroup_id();
            if( parentGroup_Id != 0) {
                UserGroup userGroup = findUserGroupById(parentGroup_Id);
                if (userGroup == null) {
                    throw new DoesNotExistException(MessagesAndContent.GROUP_M02);
                }
                newGroupRequest.setParentGroup_id(parentGroup_Id);
            }
            else {
                newGroupRequest.setParentGroup_id(0L);
            }

            long currentTime = Instant.now().getEpochSecond();

            newGroupRequest.setCreatedBy(DataUtil.getUserName());
            newGroupRequest.setCreatedDateTime(currentTime);
            newGroupRequest.setStatus(USER_STATUS_ACTIVE);
            newGroupRequest.setMember_count(0);

            UserGroup savedUserGroup = userGroupRepository.save(newGroupRequest);

            UserGroupDto userGroupDto = convertUserGroupToUserGroupResponseDto(savedUserGroup);

            return userGroupDto;

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public UserGroupDto update(long group_Id, UserGroupRequestDto group) throws CustomException {
        try {
            if(group == null ) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }

            Optional<UserGroup> foundUserGroup = userGroupRepository.findById(group_Id);


            if(!foundUserGroup.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.GROUP_M03);
            }

            UserGroup currentUserGroup = foundUserGroup.get();

            String name = InputValidatorUtil.validateStringProperty(MessagesAndContent.GROUP_M01, group.getName(), "group name", 50 );
            int foundGroup = userGroupRepository.countUserGroupsByNameEquals(name);
            if(foundGroup > 0 && !foundUserGroup.get().getName().equals(group.getName())) {
                throw new AlreadyExistException("A User Group with name " + name + " already exists.");
            }

            currentUserGroup.setName(name);
            currentUserGroup.setParentGroup_id(group.getParentGroup_id());

            String parent_group_name = "";
            parent_group_name = userGroupRepository.getParentName(group.getParentGroup_id());
            currentUserGroup.setParent_group_name(parent_group_name);

            UserGroup updateUserGroup = userGroupRepository.save(currentUserGroup);
            updateUserGroup.setMember_count(userGroupGroupsRepository.countByUserGroup(foundUserGroup.get().getId()));

            UserGroupDto userGroupDto = convertUserGroupToUserGroupResponseDto(updateUserGroup);

            return userGroupDto;

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public UserGroupDto findGroupById(long group_Id) throws CustomException {
        try {
            Optional<UserGroup> foundGroups = userGroupRepository.findById(group_Id);

            if(foundGroups.isPresent()) {
                UserGroup foundGroup = foundGroups.get();
                UserGroupDto userGroupDto = convertUserGroupToUserGroupResponseDto(foundGroup);
                return userGroupDto;
            } else {
                throw new DoesNotExistException(MessagesAndContent.GROUP_M03);
            }

        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public UserGroup findUserGroupById(long group_Id) throws CustomException {
        try {
            Optional<UserGroup> foundGroups = userGroupRepository.findById(group_Id);

            if(foundGroups.isPresent()) {
                UserGroup foundGroup = foundGroups.get();
                return foundGroup;
            } else {
                throw new DoesNotExistException(MessagesAndContent.GROUP_M03);
            }

        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public List<UserGroup> getAll() throws CustomException {
        try {
            return userGroupRepository.findAllByStatusEquals("Active");
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public PaginationDto<UserGroup> getPaginatedList(RequestListDto requestList) throws CustomException {
        try {

            if (requestList == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }
            PaginationDto<UserGroup> paginationDto = new PaginationDto<>();
            List<UserGroup> list = new ArrayList<>();
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

            String sql = FilterUtil.generateListSql(TABLE_USER_GROUP,  propertyFilterDtoList, requestList.getOrderFields(), true, page, limit);
            System.out.println(sql);
            System.out.println(requestList.getPage());
            list = customQueryRepository.getResultList(sql, UserGroup.class);

            for (UserGroup userGroup : list){
                int member_count = 0;
                member_count = userGroupGroupsRepository.countByUserGroup(userGroup.getId());
                System.out.println(member_count);
                userGroup.setMember_count(member_count);

                String parent_group_name = "";
                parent_group_name = userGroupRepository.getParentName(userGroup.getParentGroup_id());
                userGroup.setParent_group_name(parent_group_name);
            }
            paginationDto.setData(list);
            paginationDto.setTotalSize(userGroupRepository.countUserGroupsByStatusEquals("Active"));
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
    public void delete(long group_id) throws CustomException {
        try {
            Optional<UserGroup> foundGroups = userGroupRepository.findById(group_id);

            if(foundGroups.isPresent()) {
                userGroupRepository.delete(foundGroups.get());
            } else {
                throw new DoesNotExistException(MessagesAndContent.GROUP_M03);
            }

        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public UserGroupDto convertUserGroupToUserGroupResponseDto(UserGroup group) throws CustomException {
        UserGroupDto userGroupDto = new UserGroupDto();
        BeanUtils.copyProperties(group, userGroupDto);
        return userGroupDto;
    }

    @Override
    public UserGroup inactive(long group_id) throws CustomException {
        try {
            Optional<UserGroup> foundUserGroup = userGroupRepository.findById(group_id);
            if (!foundUserGroup.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.GROUP_M03);
            }
            UserGroup currentUserGroup = foundUserGroup.get();
            if (currentUserGroup.getStatus().equals(USER_STATUS_INACTIVE)) {
                throw new AlreadyExistException(MessagesAndContent.GROUP_M04);
            }

            currentUserGroup.setStatus(USER_STATUS_INACTIVE);
            UserGroup updatedUserGroup = userGroupRepository.save(currentUserGroup);

            // update history? with message user group deactivated.
            return updatedUserGroup;

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public long getCount(RequestListCountDto requestListCount) throws CustomException {
        try {

            long count = 0;

            if (requestListCount == null) {
                count = userGroupRepository.count();
            } else {
                String sql = FilterUtil.generateCountSql(TABLE_USER_GROUP, requestListCount.getFilterData());
                count = customQueryRepository.getResultListCount(sql);
            }

            return count;
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            String err = "";
            if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause().getMessage() != null) {
                err = ex.getCause().getCause().getMessage();
            }
            throw new InvalidFilterInputException("Sql query execute failed. " + err);
        }
    }
}
