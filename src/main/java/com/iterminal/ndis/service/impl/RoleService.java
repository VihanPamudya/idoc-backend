/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.service.impl;

import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.model.*;
import com.iterminal.exception.InvalidFilterInputException;
import com.iterminal.exception.AlreadyExistException;
import com.iterminal.exception.RecordNotFoundException;
import com.iterminal.exception.InvalidInputException;
import com.iterminal.exception.DoesNotExistException;
import com.iterminal.exception.UnknownException;
import com.iterminal.exception.CustomException;
import com.google.gson.Gson;
import com.iterminal.ndis.dto.PermissionDto;
import com.iterminal.ndis.repository.CustomQueryRepository;
import com.iterminal.ndis.repository.IFeatureActionRepository;
import com.iterminal.ndis.repository.IPackageRepository;
import com.iterminal.ndis.service.IUserService;
import com.iterminal.ndis.util.InputValidatorUtil;
import com.iterminal.ndis.util.MessagesAndContent;
import com.iterminal.searchfilters.RequestListDto;
import java.time.Instant;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.iterminal.ndis.service.IRoleService;
import com.iterminal.ndis.repository.IRoleHistoryRepository;
import com.iterminal.ndis.repository.IRoleRepository;
import com.iterminal.ndis.util.DataUtil;
import com.iterminal.ndis.repository.IRoleFeatureActionRepository;
import com.iterminal.searchfilters.FilterUtil;
import com.iterminal.searchfilters.RequestListCountDto;


@Slf4j
@Service
public class RoleService implements IRoleService {

    private final IRoleRepository roleRepository;
    private final IPackageRepository packageRepository;
    private final IRoleFeatureActionRepository roleFeatureActionRepository;
    private final IFeatureActionRepository featureActionRepository;
    private final IRoleHistoryRepository roleHistoryRepository;
    private final CustomQueryRepository<Role> customQueryRepository;
    private final IUserService userService;

    final String ROLE_STATUS_ACTIVE = "Active";
    final String ROLE_STATUS_INACTIVE = "Inactive";

    final String FEATURE_ACTION_ALL = "*";

    private final String ROLE_ACTION_CREATE = "Create";
    private final String ROLE_ACTION_INACTIVE = "Inactive";
    private final String ROLE_ACTION_ACTIVE = "Active";
    private final String ROLE_ACTION_UPDATE = "Update";

    Gson gson = new Gson();

    @Autowired
    public RoleService(IRoleRepository roleRepository,
                       IRoleFeatureActionRepository roleFeatureActionRepository,
                       IFeatureActionRepository featureActionRepository,
                       IRoleHistoryRepository roleHistoryRepository,
                       IPackageRepository packageRepository,
                       CustomQueryRepository customQueryRepository,
                       IUserService userService) {
        this.roleRepository = roleRepository;
        this.roleFeatureActionRepository = roleFeatureActionRepository;
        this.roleHistoryRepository = roleHistoryRepository;
        this.featureActionRepository = featureActionRepository;
        this.packageRepository = packageRepository;
        this.customQueryRepository = customQueryRepository;
        this.userService = userService;
    }

    @Override
    public Role save(Role role) throws CustomException {
        try {
            ArrayList<String> featureActionList = new ArrayList<>();
            if (role == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }

            String name = InputValidatorUtil.validateStringProperty(MessagesAndContent.ROLE_M01, role.getName(), "Name", 50);
            Optional<Role> foundRole = roleRepository.findByName(name);

            if (foundRole.isPresent()) {
                throw new AlreadyExistException(MessagesAndContent.ROLE_M03);
            }

            if (!role.isAllPermissions()) {

                String permissions = InputValidatorUtil.validateStringProperty(MessagesAndContent.ROLE_M02, role.getPermissionData(), "Permission Data", 100000);
                role.setPermissionData(permissions);

                try {
                    PermissionDto permissionData = gson.fromJson(permissions, PermissionDto.class);
                    featureActionList = DataUtil.getFeatureActionList(permissionData);
                } catch (Exception e) {
                    throw new InvalidInputException(MessagesAndContent.ROLE_M07);
                }

            } else {
                featureActionList.add(FEATURE_ACTION_ALL);
                role.setPermissionData("");
            }

            long currentTime = Instant.now().getEpochSecond();
            String userName = DataUtil.getUserName();
            role.setName(name);
            role.setStatus(ROLE_STATUS_ACTIVE);
            role.setCreatedBy(userName);
            role.setCreatedDateTime(currentTime);

            Role savedRole = roleRepository.save(role);

            for (String featureAction : featureActionList) {
                FeatureAction current;
                Optional<FeatureAction> foundFeatureAction = featureActionRepository.findByName(featureAction);
                if (!foundFeatureAction.isPresent()) {
                    FeatureAction fa = new FeatureAction();
                    fa.setName(featureAction);
                    current = featureActionRepository.save(fa);
                } else {
                    current = foundFeatureAction.get();
                }
                RoleFeatureAction rfa = new RoleFeatureAction();
                rfa.setFeatureAction(current);
                rfa.setRoleId(savedRole.getId());
                roleFeatureActionRepository.save(rfa);

            }

            savedRole = roleRepository.findById(role.getId()).get();

            updateHistory(ROLE_ACTION_CREATE, savedRole, "Role has been created.");
            return savedRole;

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public Role findById(Long id) throws CustomException {

        try {

            Optional<Role> foundRoles = roleRepository.findById(id);
            if (foundRoles.isPresent()) {
                Role role = foundRoles.get();
                if (role.isAllPermissions()) {
                    PermissionDto permission = new PermissionDto();
                    permission.setPackageList(packageRepository.findAll());
                    role.setPermissions(permission);
                }
                return role;
            } else {
                throw new DoesNotExistException(MessagesAndContent.ROLE_M04);
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
    public List<Role> getAll() throws CustomException {

        try {
            List<Role> roleList = new ArrayList<>();
            roleList = roleRepository.findAll(Sort.by(Sort.Direction.DESC, "createdDateTime"));
            if (!roleList.isEmpty()) {
                return roleList;
            } else {
                throw new RecordNotFoundException("No Records Available");
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
    public long getCount(RequestListCountDto requestListCount) throws CustomException {
        try {
            final String TABLE_ROLE = "role";
            long count = 0;

            if (requestListCount == null) {
                count = roleRepository.count();
            } else {
                String sql = FilterUtil.generateCountSql(TABLE_ROLE, requestListCount.getFilterData());
                count = customQueryRepository.getResultListCount(sql);
            }

            return count;
        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            String err = "";
            if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause().getMessage() != null) {
                err = ex.getCause().getCause().getMessage();
            }
            log.error(ex.getMessage());
            throw new InvalidFilterInputException("Sql query execute failed. " + err);
        }
    }

    @Override
    public PaginationDto<Role> getPaginatedList(RequestListDto requestList) throws CustomException {
        try {
            if (requestList == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }

            List<Role> list = new ArrayList<Role>();
            final String TABLE_ROLE = "role";
            int page = requestList.getPage();
            int limit = requestList.getLimit();
            if (page <= 0) {
                page = 1;
            }
            if (limit <= 0) {
                limit = 20;
            }
            String sql = FilterUtil.generateListSql(TABLE_ROLE, requestList.getFilterData(), requestList.getOrderFields(), requestList.isDescending(), page, limit);
            list = customQueryRepository.getResultList(sql, Role.class);

            PaginationDto<Role> paginationDto = new PaginationDto<>();

            paginationDto.setData(list);
            paginationDto.setTotalSize(roleRepository.count());
            return paginationDto;
        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            String err = "";
            if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause().getMessage() != null) {
                err = ex.getCause().getCause().getMessage();
            }
            log.error(ex.getMessage());
            throw new InvalidFilterInputException("Sql query execute failed. " + err);
        }
    }

    @Override
    public Role update(Long id, Role role) throws CustomException {

        try {
            ArrayList<String> featureActionList = new ArrayList<>();
            if (role == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }

            Optional<Role> foundUser = roleRepository.findById(id);

            if (!foundUser.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.ROLE_M04);
            }

            Role currentRole = foundUser.get();

            if (!role.isAllPermissions()) {

                String permissions = InputValidatorUtil.validateStringProperty(MessagesAndContent.ROLE_M02, role.getPermissionData(), "Permission Data", 100000);
                role.setPermissionData(permissions);

                try {
                    PermissionDto permissionData = gson.fromJson(permissions, PermissionDto.class);
                    featureActionList = DataUtil.getFeatureActionList(permissionData);

                } catch (Exception e) {
                    throw new InvalidInputException(MessagesAndContent.ROLE_M07);
                }

            } else {
                featureActionList.add(FEATURE_ACTION_ALL);
                role.setPermissionData("");
            }

            String name = InputValidatorUtil.validateStringProperty(MessagesAndContent.ROLE_M01, role.getName(), "Name", 150);
            currentRole.setName(name);

            String description = InputValidatorUtil.validateStringProperty(MessagesAndContent.ROLE_M09, role.getDescription(), "Description", 150);
            currentRole.setDescription(description);

            currentRole.setPermissionData(role.getPermissionData());
            currentRole.setAllPermissions(role.isAllPermissions());
            String userName = DataUtil.getUserName();
            if (currentRole.isLocked() && !currentRole.getLockedBy().equals(userName)) {
                throw new AlreadyExistException(MessagesAndContent.COMMON_M03);
            }

            long lockedDateTime = 0;
            currentRole.setLocked(false);
            currentRole.setLockedDateTime(lockedDateTime);
            currentRole.setLockedBy("");

            Role updatedRole = roleRepository.save(currentRole);

            List<RoleFeatureAction> roleFeatureActionList = roleFeatureActionRepository.findByRoleId(currentRole.getId());
            if (roleFeatureActionList != null && roleFeatureActionList.size() > 0) {
                roleFeatureActionRepository.deleteAllInBatch(roleFeatureActionList);
            }

            for (String featureAction : featureActionList) {
                FeatureAction current;
                Optional<FeatureAction> foundFeatureAction = featureActionRepository.findByName(featureAction);
                if (!foundFeatureAction.isPresent()) {
                    FeatureAction fa = new FeatureAction();
                    fa.setName(featureAction);
                    current = featureActionRepository.save(fa);
                } else {
                    current = foundFeatureAction.get();
                }
                RoleFeatureAction rfa = new RoleFeatureAction();
                rfa.setFeatureAction(current);
                rfa.setRoleId(updatedRole.getId());
                roleFeatureActionRepository.save(rfa);

            }

            updatedRole = roleRepository.findById(role.getId()).get();

            updateHistory(ROLE_ACTION_UPDATE, updatedRole, "Role has been updated.");

            return updatedRole;

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }

    }

    @Override
    public void delete(Long id) throws CustomException {

        try {

            Optional<Role> foundRole = roleRepository.findById(id);

            if (foundRole.isPresent()) {
                roleRepository.delete(foundRole.get());
            } else {
                throw new DoesNotExistException(MessagesAndContent.ROLE_M04);
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
    public Role inactive(Long id) throws CustomException {
        try {

            Optional<Role> foundRole = roleRepository.findById(id);

            if (!foundRole.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.ROLE_M04);
            }

            Role currentRole = foundRole.get();

            if (currentRole.getStatus().equals(ROLE_STATUS_INACTIVE)) {
                throw new AlreadyExistException(MessagesAndContent.ROLE_M05);
            }

            long count = customQueryRepository.getUserCountByRoleId(currentRole.getId());

            if (count > 0) {
                throw new AlreadyExistException(MessagesAndContent.ROLE_M08);
            }

            currentRole.setStatus(ROLE_STATUS_INACTIVE);

            Role updatedRole = roleRepository.save(currentRole);
            updateHistory(ROLE_ACTION_INACTIVE, updatedRole, "Role has been inactived.");

            return updatedRole;

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public Role active(Long id) throws CustomException {
        try {

            Optional<Role> foundRole = roleRepository.findById(id);

            if (!foundRole.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.ROLE_M04);
            }

            Role currentRole = foundRole.get();

            if (currentRole.getStatus().equals(ROLE_STATUS_ACTIVE)) {
                throw new AlreadyExistException(MessagesAndContent.ROLE_M06);
            }

            currentRole.setStatus(ROLE_STATUS_ACTIVE);

            Role updatedRole = roleRepository.save(currentRole);

            updateHistory(ROLE_ACTION_ACTIVE, updatedRole, "Role has been actived.");

            return updatedRole;

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public List<RoleHistory> getRoleHistory(Long id) throws CustomException {
        try {
            return roleHistoryRepository.findByRoleId(id, Sort.by(Sort.Direction.DESC, "performedDateTime"));

        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public void lock(Long id) throws CustomException {
        try {

            Optional<Role> foundRole = roleRepository.findById(id);
            String userName = DataUtil.getUserName();
            if (!foundRole.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.ROLE_M04);
            }

            Role currentRole = foundRole.get();

            if (currentRole.isLocked() && currentRole.getLockedBy().equals(userName)) {
                throw new AlreadyExistException(MessagesAndContent.COMMON_M06);
            }

            if (currentRole.isLocked() && !currentRole.getLockedBy().equals(userName)) {
                throw new AlreadyExistException(MessagesAndContent.COMMON_M03);
            }

            long currentTime = Instant.now().getEpochSecond();
            currentRole.setLocked(true);
            currentRole.setLockedDateTime(currentTime);
            currentRole.setLockedBy(userName);

            roleRepository.save(currentRole);

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public void unlock(Long id) throws CustomException {
        try {
            String userName = DataUtil.getUserName();
            Optional<Role> foundRole = roleRepository.findById(id);

            if (!foundRole.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.ROLE_M04);
            }

            Role currentRole = foundRole.get();

            if (!currentRole.isLocked()) {
                throw new DoesNotExistException(MessagesAndContent.COMMON_M05);
            }

            if (!currentRole.getLockedBy().equals(userName)) {
                throw new DoesNotExistException(MessagesAndContent.COMMON_M04);
            }

            long lockedDateTime = 0;
            currentRole.setLocked(false);
            currentRole.setLockedDateTime(lockedDateTime);
            currentRole.setLockedBy("");
            roleRepository.save(currentRole);

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    // cron: seconds / minutes / hours / day of the month / month / day of the week
    // Current cron: Every hour
    // Check cron: https://crontab.guru/  |   https://crontab.cronhub.io/
    // test : @Scheduled(initialDelay = 1000L, fixedRate = 15000L)
    @Scheduled(cron = "0 0 * * * *")
    protected void unlockRoleAutomatically() {
        List<Role> roleListByLockedStatus = roleRepository.findAllByLocked(true);

        if (roleListByLockedStatus != null) {
            for (Role foundRole : roleListByLockedStatus) {

                long currentTime = Instant.now().getEpochSecond();
                long diff = TimeUnit.SECONDS.toHours(currentTime - foundRole.getLockedDateTime());

                if (diff >= 1) {
                    long lockedDateTime = 0;
                    foundRole.setLocked(false);
                    foundRole.setLockedDateTime(lockedDateTime);
                    foundRole.setLockedBy("");
                    log.info("Unlocked the Role: " + foundRole.getName() + ", automatically by scheduled task at " + System.currentTimeMillis());
                    roleRepository.save(foundRole);
                }
            }
        }
    }

    private void updateHistory(String action, Role role, String description) throws CustomException {

        String userName = DataUtil.getUserName();
        String performedUserFullName = userService.getUserFullNameByUserName(userName);
        long currentTime = Instant.now().getEpochSecond();
        RoleHistory roleHistory = new RoleHistory();
        roleHistory.setAction(action);
        roleHistory.setAction(action);
        roleHistory.setRoleId(role.getId());
        roleHistory.setName(role.getName());
        roleHistory.setDescription(description);
        roleHistory.setPerformedBy(performedUserFullName);
        roleHistory.setPerformedDateTime(currentTime);
        roleHistory.setStatus(role.getStatus());

        roleHistoryRepository.save(roleHistory);

    }

}
