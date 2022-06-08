package com.iterminal.ndis.service.impl;

import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.dto.response.UserBasicDto;
import com.iterminal.ndis.model.*;
import com.iterminal.ndis.repository.*;
import com.iterminal.ndis.dto.response.NotificationDto;
import com.iterminal.ndis.dto.response.UserDto;
import com.iterminal.ndis.dto.PermissionDto;
import com.iterminal.ndis.dto.RequestChanagePasswordDto;
import com.iterminal.ndis.dto.UserRequestDto;
import com.iterminal.exception.AlreadyExistException;
import com.iterminal.exception.CustomException;
import com.iterminal.exception.DoesNotExistException;
import com.iterminal.exception.InvalidFilterInputException;
import com.iterminal.exception.InvalidInputException;
import com.iterminal.exception.UnknownException;
import com.iterminal.ndis.service.IOrganizationService;
import com.iterminal.ndis.service.email.EmailService;
import com.iterminal.ndis.util.DataUtil;
import com.iterminal.ndis.util.InputValidatorUtil;
import com.iterminal.ndis.util.MessagesAndContent;
import com.iterminal.ndis.util.PasswordEncriptor;
import com.iterminal.searchfilters.*;

import java.time.Instant;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.iterminal.ndis.service.IUserService;

import java.util.concurrent.TimeUnit;

import static com.iterminal.ndis.util.MessagesAndContent.*;

@Slf4j
@Service
public class UserService implements IUserService {

    private final CustomQueryRepository<User> customQueryRepository;
    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;
    private final IUserHistoryRepository userHistoryRepository;
    private final IUserPasswordHistoryRepository userPasswordHistoryRepository;
    private final IOrganizationService organizationService;
    private final IPackageRepository packageRepository;
    private final IRoleRepository roleRepository;
    private final IUserGroupRepository groupRepository;
    private final IUserGroupGroupsRepository groupGroupsRepository;
    private final INotificationRepository notificationRepository;
    private final SqlCustomQueryRepository sqlCustomQueryRepository;

    final String TABLE_DESIGNATION = "designation";

    private EmailService emailService;

    final String USER_STATUS_ACTIVE = "Active";
    final String USER_STATUS_INACTIVE = "Inactive";
    final String USER_STATUS_BLOCKED = "Block";

    private final String USER_ACTION_CREATE = "Create";
    private final String USER_ACTION_INACTIVE = "Inactive";
    private final String USER_ACTION_ACTIVE = "Active";
    private final String USER_ACTION_UPDATE = "Update";
    private final String USER_ACTION_BLOCK = "Block";
    private final String USER_ACTION_UNBLOCK = "Unblock";
    final int MAX_NO_OF_ATTEMPTS = 3;

    final String TABLE_USER = "user";

    String token = null;

    @Value("${user.inactivation.days}")
    private long userInactivationDays;

    @Value("${ndis.url}")
    private String ndisUrl;

    @Autowired
    public UserService(IUserRepository userRepository,
                       CustomQueryRepository customQueryRepository,
                       IUserPasswordHistoryRepository userPasswordHistoryRepository,
                       IUserHistoryRepository userHistoryRepository,
                       IUserRoleRepository userRoleRepository,
                       IUserGroupRepository groupRepository,
                       IUserGroupGroupsRepository groupGroupsRepository,
                       EmailService emailService,
                       IRoleRepository roleRepository,
                       IOrganizationService organizationService,
                       IPackageRepository packageRepository,
                       INotificationRepository notificationRepository,
                       SqlCustomQueryRepository sqlCustomQueryRepository) {
        this.userRepository = userRepository;
        this.userHistoryRepository = userHistoryRepository;
        this.userPasswordHistoryRepository = userPasswordHistoryRepository;
        this.roleRepository = roleRepository;
        this.customQueryRepository = customQueryRepository;
        this.emailService = emailService;
        this.userRoleRepository = userRoleRepository;
        this.organizationService = organizationService;
        this.packageRepository = packageRepository;
        this.notificationRepository = notificationRepository;
        this.sqlCustomQueryRepository = sqlCustomQueryRepository;
        this.groupRepository = groupRepository;
        this.groupGroupsRepository = groupGroupsRepository;
    }

    @Override
    public UserDto create(UserRequestDto request) throws CustomException {
        try {

            User userRequest = new User();

            if (request == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }

            String epfNumber = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M01, request.getEpfNumber(), "Epf Number", 50);
            userRequest.setEpfNumber(epfNumber);

            long currentTime = Instant.now().getEpochSecond();


            String userName = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, request.getUserName(), "User Name", 150);
            Optional<User> foundUserName = userRepository.findByName(userName);
            if(foundUserName.isPresent()) {
                throw new AlreadyExistException("A User with name " + userName + " already exists.");
            }
            userRequest.setUserName(userName);

            String firstName = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M03, request.getFirstName(), "First Name", 150);
            userRequest.setFirstName(firstName);

            String lastName = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M37, request.getLastName(), "Last Name", 150);
            userRequest.setLastName(lastName);

            String storageQuota = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M05, request.getStorageQuota(), "Storage Quota", 150);
            userRequest.setStorageQuota(storageQuota);

            String gender = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M06, request.getGender(), "Gender", 150);
            userRequest.setGender(gender);

            String dateOfBirth = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M07, request.getDateOfBirth(), "Date of Birth", 150);
            userRequest.setDateOfBirth(dateOfBirth);

            String address = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, request.getAddress(), "Address", 150);
            userRequest.setAddress(address);

            String email = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M05, request.getEmail(), "Email", 50);
            userRequest.setEmail(email);

            if (userRepository.countByEmail(email) > 0) {
                throw new AlreadyExistException(MessagesAndContent.USER_M034);
            }

            String mobileNo = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M06, request.getMobileNumber(), "Mobile Number", 15);
            userRequest.setMobileNumber(mobileNo);

            if (!InputValidatorUtil.isValidEmail(email)) {
                throw new InvalidInputException(MessagesAndContent.USER_M08);
            }

            String password = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M015, request.getPassword(), "Password", 100);
            String encriptPassword = PasswordEncriptor.encriptPassword(password);

            if (userRepository.existsById(epfNumber)) {
                throw new AlreadyExistException(MessagesAndContent.USER_M07);
            }

            // UserRole

            if (request.getRoleList() == null) {
                throw new InvalidInputException("userRole null");
            }
            System.out.println(request.getRoleList());
            if (request.getRoleList().isEmpty()) {
                throw new InvalidInputException("userRole Empty");
            }

            for (UserRole userRole : request.getRoleList()) {

                Role role = userRole.getRole();
                if (role == null) {
                    throw new InvalidInputException(MessagesAndContent.USER_M031);
                }
                if (role.getId() == null) {
                    throw new InvalidInputException(MessagesAndContent.USER_M031);
                }
                Optional<Role> foundRoles = roleRepository.findById(role.getId());
                if (!foundRoles.isPresent()) {
                    throw new DoesNotExistException(MessagesAndContent.ROLE_M04);
                } else {
                    role = foundRoles.get();
                }
                userRole.setRole(role);
            }

            // UserGroup

            if (request.getGroupList() == null) {
                throw new InvalidInputException("userGroup null");
            }
            System.out.println(request.getGroupList());
            if (request.getGroupList().isEmpty()) {
                throw new InvalidInputException("userGroup Empty");
            }

            for (UserGroupGroups userGroupGroups : request.getGroupList()) {

                UserGroup userGroup = userGroupGroups.getUserGroup();
                if (userGroup == null) {
                    throw new InvalidInputException(MessagesAndContent.USER_M031);
                }
                if (userGroup.getId() == null) {
                    throw new InvalidInputException(MessagesAndContent.USER_M031);
                }
                Optional<UserGroup> foundGroups = groupRepository.findById(userGroup.getId());
                if (!foundGroups.isPresent()) {
                    throw new DoesNotExistException(MessagesAndContent.ROLE_M04);
                } else {
                    userGroup = foundGroups.get();
                }
                userGroupGroups.setUserGroup(userGroup);
            }

            long lastSignIn = 0;
            userRequest.setStatus(USER_STATUS_ACTIVE);
            userRequest.setCreatedBy(DataUtil.getUserName());
            userRequest.setCreatedDateTime(currentTime);
            userRequest.setNoOfAttempts(0);
            userRequest.setSignIn(false);
            userRequest.setLastSignIn(lastSignIn);
            userRequest.setPassword(encriptPassword);
            userRequest.setToken("");
            userRequest.setRefreshToken("");
            userRequest.setFirstTime(true);

            User savedUser = userRepository.save(userRequest);

            //sentEmail
            try {
                String messageBody = "Hey " + savedUser.getUserName() + ",\n"
                        + "\n"
                        + "Welcome to iDoc. Your account has been created. Here are your credentials.\n"
                        + "Sign-in Details Username: Use your EPF number as the username. Temporary Password: " + password + " \n"
                        + "\n"
                        + ndisUrl + " - iDoc Administration";
                emailService.send(savedUser.getEmail(), "Create a new user account", messageBody, null);
            } catch (Exception ex) {
                log.error(ex.getMessage());
                //sent Email error
            }

            // UserRole
            List<UserRole> saveRoleList = new ArrayList<>();

            if (request.getRoleList() != null && request.getRoleList().size() > 0) {
                for (UserRole userRole : request.getRoleList()) {
                    userRole.setEpfNumber(epfNumber);
                    if (userRole.getRole() != null && userRole.getRole().getId() != null && userRole.getRole().getId() > 0) {
                        UserRole saveUserRole = userRoleRepository.save(userRole);
                        if (saveUserRole.getRole().isAllPermissions()) {
                            PermissionDto permission = new PermissionDto();
                            permission.setPackageList(packageRepository.findAll());
                            saveUserRole.getRole().setPermissions(permission);
                        }
                        saveRoleList.add(saveUserRole);
                    }

                }
            }

            // UserGroup
            List<UserGroupGroups> saveGroupList = new ArrayList<>();

            if (request.getGroupList() != null && request.getGroupList().size() > 0) {
                for (UserGroupGroups userGroupGroups : request.getGroupList()) {
                    userGroupGroups.setEpfNumber(epfNumber);
                    if (userGroupGroups.getUserGroup() != null && userGroupGroups.getUserGroup().getId() != null && userGroupGroups.getUserGroup().getId() > 0) {
                        UserGroupGroups saveUserGroup = groupGroupsRepository.save(userGroupGroups);
                        saveGroupList.add(saveUserGroup);
                    }

                }
            }

            updateHistory(USER_ACTION_CREATE, savedUser, "User account has been created.");


            UserDto userDto = convertUserToUserResponseDto(savedUser);
//            userDto.setOrganization(organization.getName());
            userDto.setRoleList(saveRoleList);
            userDto.setGroupList(saveGroupList);

            return userDto;

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }

    }

    @Override
    public UserDto findUserById(String epfNumber) throws CustomException {

        try {

            epfNumber = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M01, epfNumber);

            Optional<User> foundUsers = userRepository.findById(epfNumber);

            if (foundUsers.isPresent()) {

                User foundUser = foundUsers.get();
                UserDto userDto = convertUserToUserResponseDto(foundUser);
                return userDto;
            } else {
                log.error("Cannot find the user by given EPF number: " + epfNumber);
                throw new DoesNotExistException(MessagesAndContent.USER_M09);
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
    public String getUserFullNameByUserName(String userName) throws CustomException {
        try {
            UserDto foundUser = findUserById(userName);

            String fullName = foundUser.getUserName();

            return fullName;
        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public UserDto convertUserToUserResponseDto(User user) throws CustomException {

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
//        if (user.getOrganizationId() != 0) {
//            userDto.setOrganization(organizationService.findById(user.getOrganizationId()).getName());
//        }
        List<UserRole> userRoleList = customQueryRepository.getUserRoleList(user.getEpfNumber());
        if (userRoleList != null && userRoleList.size() > 0) {
            for (UserRole userRole : userRoleList) {
                if (userRole.getRole().isAllPermissions()) {
                    PermissionDto permission = new PermissionDto();
                    permission.setPackageList(packageRepository.findAll());
                    userRole.getRole().setPermissions(permission);
                }
            }
        }
        return userDto;
    }

    @Override
    public UserBasicDto convertUserToUserBasicResponseDto(User user) throws CustomException {

        UserBasicDto userBasicDto = new UserBasicDto();
        BeanUtils.copyProperties(user, userBasicDto);

        return userBasicDto;
    }

    @Override
    public List<User> getAll() throws CustomException {

        try {
            return userRepository.findAllByStatus(USER_STATUS_ACTIVE);

        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }

    }

    @Override
    public long getCount(RequestListCountDto requestListCount) throws CustomException {
        try {

            long count = 0;

            if (requestListCount == null) {
                count = userRepository.count();
            } else {
                String sql = FilterUtil.generateCountSql(TABLE_USER, requestListCount.getFilterData());
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
    public PaginationDto<User> getPaginatedList(RequestListDto requestList) throws CustomException {
        try {

            if (requestList == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }
            PaginationDto<User> paginationDto = new PaginationDto<>();
            List<User> list = new ArrayList<>();
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

            String sql = FilterUtil.generateListSql(TABLE_USER, propertyFilterDtoList, requestList.getOrderFields(), requestList.isDescending(), page, limit);
            list = customQueryRepository.getResultList(sql, User.class);
            paginationDto.setData(list);
            paginationDto.setTotalSize(userRepository.countUsersByStatusEquals("Active"));
            return paginationDto;
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
    public UserDto updateUserProfile(String epfNumber, User userRequest) throws CustomException {
        try {

            if (userRequest == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }

            epfNumber = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M01, epfNumber, "Epf Number", 50);

            Optional<User> foundUser = userRepository.findById(epfNumber);

            if (!foundUser.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.USER_M09);
            }

            User currentUser = foundUser.get();

            if (userRequest.getEpfNumber() != null) {
                if (!epfNumber.equals(userRequest.getEpfNumber())) {
                    throw new InvalidInputException(MessagesAndContent.USER_M027);
                }
            }

            String userName = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, userRequest.getUserName(), "User Name", 150);
            userRequest.setUserName(userName);

            String firstName = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, userRequest.getFirstName(), "User Name", 150);
            userRequest.setFirstName(firstName);

            String lastName = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, userRequest.getLastName(), "User Name", 150);
            userRequest.setLastName(lastName);

            String storageQuota = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, userRequest.getStorageQuota(), "Storage Quota", 150);
            userRequest.setStorageQuota(storageQuota);

            String gender = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, userRequest.getGender(), "Gender", 150);
            userRequest.setGender(gender);

            String dateOfBirth = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, userRequest.getDateOfBirth(), "Date of Birth", 150);
            userRequest.setDateOfBirth(dateOfBirth);

            String address = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, userRequest.getAddress(), "Address", 150);
            userRequest.setAddress(address);

            String email = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M05, userRequest.getEmail(), "Email", 50);
            userRequest.setEmail(email);

            if (!currentUser.getEmail().equals(email)) {
                if (userRepository.countByEmail(email) > 0) {
                    throw new AlreadyExistException(MessagesAndContent.USER_M034);
                }
            }

            String mobileNo = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M06, userRequest.getMobileNumber(), "Mobile Number", 15);
            userRequest.setMobileNumber(mobileNo);

            if (!InputValidatorUtil.isValidEmail(email)) {
                throw new InvalidInputException(MessagesAndContent.USER_M08);
            }

            if (currentUser.isLocked() && !currentUser.getLockedBy().equals(DataUtil.getUserName())) {
                throw new AlreadyExistException(MessagesAndContent.COMMON_M03);
            }
            long lockedDateTime = 0;
            currentUser.setLocked(false);
            currentUser.setLockedDateTime(lockedDateTime);
            currentUser.setLockedBy("");

            currentUser.setEpfNumber(epfNumber);
            currentUser.setUserName(userRequest.getUserName());
            currentUser.setFirstName(userRequest.getFirstName());
            currentUser.setLastName(userRequest.getLastName());
            currentUser.setGender(userRequest.getGender());
            currentUser.setStorageQuota(userRequest.getStorageQuota());
            currentUser.setDateOfBirth(userRequest.getDateOfBirth());
            currentUser.setAddress(userRequest.getAddress());
            currentUser.setEmail(userRequest.getEmail());
            currentUser.setMobileNumber(userRequest.getMobileNumber());

            User updatedUser = userRepository.save(currentUser);

            updateHistory(USER_ACTION_UPDATE, updatedUser, "User profile has been updated.");

            return findUserById(epfNumber);

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }

    }

    @Override
    public UserDto update(String epfNumber, UserRequestDto userRequest) throws CustomException {

        try {

            if (userRequest == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }

            epfNumber = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M01, epfNumber, "Epf Number", 50);

            Optional<User> foundUser = userRepository.findById(epfNumber);

            if (!foundUser.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.USER_M09);
            }

            User currentUser = foundUser.get();

            if (userRequest.getEpfNumber() != null) {
                if (!epfNumber.equals(userRequest.getEpfNumber())) {
                    throw new InvalidInputException(MessagesAndContent.USER_M027);
                }
            }

            String userName = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, userRequest.getUserName(), "User Name", 150);
            Optional<User> userFound = userRepository.findByName(userName);
            if(userFound.isPresent() && !userName.equalsIgnoreCase(userRequest.getUserName())) {
                throw new AlreadyExistException("A User with name " + userName + " already exists.");
            }
            userRequest.setUserName(userName);

            String firstName = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, userRequest.getFirstName(), "User Name", 150);
            userRequest.setFirstName(firstName);

            String lastName = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, userRequest.getLastName(), "User Name", 150);
            userRequest.setLastName(lastName);

            String storageQuota = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, userRequest.getStorageQuota(), "Storage Quota", 150);
            userRequest.setStorageQuota(storageQuota);

            String gender = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, userRequest.getGender(), "Gender", 150);
            userRequest.setGender(gender);

            String dateOfBirth = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, userRequest.getDateOfBirth(), "Date of Birth", 150);
            userRequest.setDateOfBirth(dateOfBirth);

            String address = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M04, userRequest.getAddress(), "Address", 150);
            userRequest.setAddress(address);

            String email = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M05, userRequest.getEmail(), "Email", 50);
            userRequest.setEmail(email);

            String mobileNo = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M06, userRequest.getMobileNumber(), "Mobile Number", 15);
            userRequest.setMobileNumber(mobileNo);

            if (!currentUser.getEmail().equals(email)) {
                if (userRepository.countByEmail(email) > 0) {
                    throw new AlreadyExistException(MessagesAndContent.USER_M034);
                }
            }

            // UserRole
            if (userRequest.getRoleList() == null) {
                throw new InvalidInputException(MessagesAndContent.USER_M029);
            }
            if (userRequest.getRoleList().isEmpty()) {
                throw new InvalidInputException(MessagesAndContent.USER_M029);
            }

            for (UserRole userRole : userRequest.getRoleList()) {

                Role role = userRole.getRole();
                if (role == null) {
                    throw new InvalidInputException(MessagesAndContent.USER_M031);
                }
                if (role.getId() == null) {
                    throw new InvalidInputException(MessagesAndContent.USER_M031);
                }
                Optional<Role> foundRoles = roleRepository.findById(role.getId());
                if (!foundRoles.isPresent()) {
                    throw new DoesNotExistException(MessagesAndContent.ROLE_M04);
                } else {
                    role = foundRoles.get();
                }
                userRole.setRole(role);
            }

            // UserGroup
            System.out.println(userRequest.getGroupList());
            if (userRequest.getGroupList() == null) {
                throw new InvalidInputException(MessagesAndContent.USER_M036);
            }
            if (userRequest.getGroupList().isEmpty()) {
                throw new InvalidInputException(MessagesAndContent.USER_M036);
            }

            for (UserGroupGroups userGroupGroups : userRequest.getGroupList()) {

                UserGroup userGroup = userGroupGroups.getUserGroup();
                System.out.println(userGroup);
                if (userGroup == null) {
                    throw new InvalidInputException(MessagesAndContent.USER_M031);
                }
                if (userGroup.getId() == null) {
                    throw new InvalidInputException(MessagesAndContent.USER_M031);
                }
                Optional<UserGroup> foundGroups = groupRepository.findById(userGroup.getId());
                System.out.println(foundGroups);
                if (!foundGroups.isPresent()) {
                    throw new DoesNotExistException(MessagesAndContent.ROLE_M04);
                } else {
                    userGroup = foundGroups.get();
                }
                userGroupGroups.setUserGroup(userGroup);
            }

            if (!InputValidatorUtil.isValidEmail(email)) {
                throw new InvalidInputException(MessagesAndContent.USER_M08);
            }

            if (currentUser.isLocked() && currentUser.getLockedBy() != null && !currentUser.getLockedBy().equals(DataUtil.getUserName())) {
                throw new AlreadyExistException(MessagesAndContent.COMMON_M03);
            }
            long lockedDateTime = 0;
            currentUser.setLocked(false);
            currentUser.setLockedDateTime(lockedDateTime);
            currentUser.setLockedBy("");
            currentUser.setEpfNumber(epfNumber);
            currentUser.setUserName(userRequest.getUserName());
            currentUser.setFirstName(userRequest.getFirstName());
            currentUser.setLastName(userRequest.getLastName());
            currentUser.setGender(userRequest.getGender());
            currentUser.setStorageQuota(userRequest.getStorageQuota());
            currentUser.setDateOfBirth(userRequest.getDateOfBirth());
            currentUser.setAddress(userRequest.getAddress());
            currentUser.setEmail(userRequest.getEmail());
            currentUser.setMobileNumber(userRequest.getMobileNumber());

            User updatedUser = userRepository.save(currentUser);

            // UserRole
            List<UserRole> saveRoleList = new ArrayList<>();

            List<UserRole> tempUserRoleList = userRoleRepository.findByEpfNumber(epfNumber);
            if (tempUserRoleList != null && tempUserRoleList.size() > 0) {
                userRoleRepository.deleteAllInBatch(tempUserRoleList);
            }

            if (userRequest.getRoleList() != null && userRequest.getRoleList().size() > 0) {
                for (UserRole userRole : userRequest.getRoleList()) {
                    userRole.setEpfNumber(epfNumber);
                    if (userRole.getRole() != null && userRole.getRole().getId() != null && userRole.getRole().getId() > 0) {
                        UserRole saveUserRole = userRoleRepository.save(userRole);
                        if (saveUserRole.getRole().isAllPermissions()) {
                            PermissionDto permission = new PermissionDto();
                            permission.setPackageList(packageRepository.findAll());
                            saveUserRole.getRole().setPermissions(permission);
                        }
                        saveRoleList.add(saveUserRole);

                    }

                }
            }

            // UserGroup
            List<UserGroupGroups> saveGroupList = new ArrayList<>();

            List<UserGroupGroups> tempUserGroupList = groupGroupsRepository.findByEpfNumber(epfNumber);
            if (tempUserGroupList != null && tempUserGroupList.size() > 0) {
                groupGroupsRepository.deleteAllInBatch(tempUserGroupList);
            }

            if (userRequest.getGroupList() != null && userRequest.getGroupList().size() > 0) {
                for (UserGroupGroups userGroupGroups : userRequest.getGroupList()) {
                    userGroupGroups.setEpfNumber(epfNumber);

                    if (userGroupGroups.getUserGroup() != null && userGroupGroups.getUserGroup().getId() != null && userGroupGroups.getUserGroup().getId() > 0) {

                        UserGroupGroups saveUserGroup = groupGroupsRepository.save(userGroupGroups);
                        System.out.println(userGroupGroups);
                        saveGroupList.add(saveUserGroup);

                    }

                }
            }

            updateHistory(USER_ACTION_UPDATE, updatedUser, "User account has been updated.");

            UserDto userDto = convertUserToUserResponseDto(updatedUser);
            userDto.setRoleList(saveRoleList);
            userDto.setGroupList(saveGroupList);

            return userDto;

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public void delete(String epfNumber) throws CustomException {

        try {

            epfNumber = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M01, epfNumber);

            Optional<User> foundUser = userRepository.findById(epfNumber);

            if (foundUser.isPresent()) {
                userRepository.delete(foundUser.get());
            } else {
                throw new DoesNotExistException(MessagesAndContent.USER_M09);
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
    public User active(String epfNumber) throws CustomException {
        try {

            epfNumber = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M01, epfNumber);

            Optional<User> foundUser = userRepository.findById(epfNumber);

            if (!foundUser.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.USER_M09);
            }
            User currentUser = foundUser.get();

            if (currentUser.getStatus().equals(USER_STATUS_ACTIVE)) {
                throw new AlreadyExistException(MessagesAndContent.USER_M010);
            }

            currentUser.setStatus(USER_STATUS_ACTIVE);

            User updatedUser = userRepository.save(currentUser);

            updateHistory(USER_ACTION_ACTIVE, updatedUser, "User account has been reactivated.");

            //sentEmail
            try {
                String messageBody = "Hey " + updatedUser.getUserName() + ",\n"
                        + "\n"
                        + "Your user account of the Office for National Development Information System has been reactivated.\n"
                        + "\n"
                        + ndisUrl + "- Office for National Development Information System Administration";

                emailService.send(updatedUser.getEmail(), "Reactivation a user profile", messageBody, null);

            } catch (Exception ex) {
                log.error(ex.getMessage());
            }

            return updatedUser;

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public User inactive(String epfNumber) throws CustomException {
        try {

            epfNumber = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M01, epfNumber);

            Optional<User> foundUser = userRepository.findById(epfNumber);

            if (!foundUser.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.USER_M09);
            }
            User currentUser = foundUser.get();

            if (currentUser.getStatus().equals(USER_STATUS_INACTIVE)) {
                throw new AlreadyExistException(MessagesAndContent.USER_M011);
            }

            if (currentUser.getStatus().equals(USER_STATUS_BLOCKED)) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M02);
            }

            currentUser.setStatus(USER_STATUS_INACTIVE);

            User updatedUser = userRepository.save(currentUser);

            updateHistory(USER_ACTION_INACTIVE, updatedUser, "User account has been deactivated.");

            //sentEmail
            try {
                String messageBody = "Hey " + updatedUser.getUserName() + ",\n"
                        + "\n"
                        + "Your user account of the Office for National Development Information System has been deactivated.\n"
                        + "If you want to reactivate your account, please contact the User Administrator on the administration helpline.\n"
                        + "\n"
                        + ndisUrl + "- Office for National Development Information System Administration";
                emailService.send(updatedUser.getEmail(), "Inactivation of a user profile", messageBody, null);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }

            return updatedUser;

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public User block(String epfNumber) throws CustomException {
        try {

            epfNumber = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M01, epfNumber);

            Optional<User> foundUser = userRepository.findById(epfNumber);

            if (!foundUser.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.USER_M09);
            }

            User currentUser = foundUser.get();

            if (currentUser.getStatus().equals(USER_STATUS_BLOCKED)) {
                throw new AlreadyExistException(MessagesAndContent.USER_M012);
            }

            currentUser.setStatus(USER_STATUS_BLOCKED);

            User updatedUser = userRepository.save(currentUser);

            updateHistory(USER_ACTION_BLOCK, updatedUser, "User account has been blocked.");

            //sentEmail
            try {
                String messageBody = "Hey " + updatedUser.getUserName() + ",\n"
                        + "\n"
                        + "Your user account of the Office for National Development Information System has been blocked.\n"
                        + "If you want to unblock your account, please contact the User Administrator on the administration helpline.\n"
                        + "\n"
                        + ndisUrl + "- Office for National Development Information System Administration";
                emailService.send(updatedUser.getEmail(), "Blocking a user profile", messageBody, null);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }

            return updatedUser;

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public User unblock(String epfNumber) throws CustomException {
        try {

            epfNumber = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M01, epfNumber);

            Optional<User> foundUser = userRepository.findById(epfNumber);

            if (!foundUser.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.USER_M09);
            }
            User currentUser = foundUser.get();

            if (currentUser.getStatus().equals(USER_STATUS_ACTIVE)) {
                throw new AlreadyExistException(MessagesAndContent.USER_M013);
            }

            if (!currentUser.getStatus().equals(USER_STATUS_BLOCKED)) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M02);
            }

            currentUser.setStatus(USER_STATUS_ACTIVE);
            currentUser.setNoOfAttempts(0);

            User updatedUser = userRepository.save(currentUser);

            updateHistory(USER_ACTION_UNBLOCK, updatedUser, "User account has been unblocked.");

            //sentEmail
            try {
                String messageBody = "Hey " + updatedUser.getUserName() + ",\n"
                        + "\n"
                        + "Your user account of the Office for National Development Information System has been unblocked.\n"
                        + "\n"
                        + ndisUrl + "- Office for National Development Information System Administration";

                emailService.send(updatedUser.getEmail(), "Unblocking a user profile", messageBody, null);

            } catch (Exception ex) {
                log.error(ex.getMessage());
            }

            return updatedUser;

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public void lock(String epfNumber) throws CustomException {
        try {

            String userName = DataUtil.getUserName();

            epfNumber = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M01, epfNumber);

            Optional<User> foundUser = userRepository.findById(epfNumber);

            if (!foundUser.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.USER_M09);
            }
            User currentUser = foundUser.get();

            if (currentUser.isLocked() && currentUser.getLockedBy().equals(userName)) {
                throw new AlreadyExistException(MessagesAndContent.COMMON_M06);
            }

            if (currentUser.isLocked() && !currentUser.getLockedBy().equals(userName)) {
                throw new AlreadyExistException(MessagesAndContent.COMMON_M03);
            }

            long currentTime = Instant.now().getEpochSecond();
            currentUser.setLocked(true);
            currentUser.setLockedDateTime(currentTime);
            currentUser.setLockedBy(userName);

            userRepository.save(currentUser);

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public void unlock(String epfNumber) throws CustomException {
        try {

            epfNumber = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M01, epfNumber);

            Optional<User> foundUser = userRepository.findById(epfNumber);

            if (!foundUser.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.USER_M09);
            }
            User currentUser = foundUser.get();

            if (!currentUser.isLocked()) {
                throw new DoesNotExistException(MessagesAndContent.COMMON_M05);
            }

            String userName = DataUtil.getUserName();

            if (!currentUser.getLockedBy().equals(userName)) {
                throw new DoesNotExistException(MessagesAndContent.COMMON_M04);
            }

            long lockedDateTime = 0;
            currentUser.setLocked(false);
            currentUser.setLockedDateTime(lockedDateTime);
            currentUser.setLockedBy("");
            userRepository.save(currentUser);

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
    protected void unlockUserAutomatically() {
        List<User> userListByLockedStatus = userRepository.findAllByLocked(true);

        if (userListByLockedStatus != null) {
            for (User foundUser : userListByLockedStatus) {

                long currentTime = Instant.now().getEpochSecond();
                long diff = TimeUnit.SECONDS.toHours(currentTime - foundUser.getLockedDateTime());

                if (diff >= 1) {
                    long lockedDateTime = 0;
                    foundUser.setLocked(false);
                    foundUser.setLockedDateTime(lockedDateTime);
                    foundUser.setLockedBy("");
                    log.info("Unlocked the User: " + foundUser.getEpfNumber() + ", automatically by scheduled task at " + System.currentTimeMillis());
                    userRepository.save(foundUser);
                }
            }
        }
    }

    @Override
    public List<UserHistory> getUserHistory(String epfNumber) throws CustomException {
        try {

            epfNumber = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M01, epfNumber);
            Optional<User> foundUser = userRepository.findById(epfNumber);

            if (!foundUser.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.USER_M09);
            }

            return userHistoryRepository.findByEpfNumber(epfNumber, Sort.by(Sort.Direction.DESC, "performedDateTime"));

        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public void changePassword(RequestChanagePasswordDto requestChanagePassword) throws CustomException {

        try {

            if (requestChanagePassword == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }

            String username = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M014, requestChanagePassword.getUsername(), "Username", 50);
            String password = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M015, requestChanagePassword.getPassword(), "Password", 20);
            String newPassword = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M016, requestChanagePassword.getNewPassword(), "New Password", 20);

            if (!userRepository.existsById(username)) {
                throw new DoesNotExistException(MessagesAndContent.USER_M017);
            }

            User currentUser = userRepository.getById(username);

            if (currentUser.getStatus().equals(USER_STATUS_BLOCKED)) {
                throw new InvalidInputException(MessagesAndContent.USER_M018);
            }
            if (currentUser.getStatus().equals(USER_STATUS_INACTIVE)) {
                throw new InvalidInputException(MessagesAndContent.USER_SIGN_IN_M07);
            }

            password = PasswordEncriptor.encriptPassword(password);

            if (!currentUser.getPassword().equals(password)) {
                int count = currentUser.getNoOfAttempts() + 1;
                int rest = MAX_NO_OF_ATTEMPTS - count;
                if (rest > 0) {
                    currentUser.setNoOfAttempts(count);
                    currentUser = userRepository.save(currentUser);
                    throw new InvalidInputException(MessagesAndContent.USER_SIGN_IN_M01.replace("<?>", rest + ""));
                } else {
                    currentUser.setNoOfAttempts(0);
                    currentUser.setStatus(USER_STATUS_BLOCKED);
                    currentUser = userRepository.save(currentUser);
                    throw new InvalidInputException(MessagesAndContent.USER_SIGN_IN_M05);
                }
            }

            if (newPassword.length() < 8) {
                throw new InvalidInputException(MessagesAndContent.USER_M020);
            }

            if (newPassword.length() > 20) {
                throw new InvalidInputException(MessagesAndContent.USER_M021);
            }

            if (!InputValidatorUtil.isValidPassword(newPassword)) {
                throw new InvalidInputException(MessagesAndContent.USER_M022);
            }
            newPassword = PasswordEncriptor.encriptPassword(newPassword);
            List<UserPasswordHistory> passwordHistory = userPasswordHistoryRepository.findByEpfNumberOrderByCreatedDateTimeDesc(username);
            if (passwordHistory != null && passwordHistory.size() > 0) {
                int size = 5;
                if (passwordHistory.size() < size) {
                    size = passwordHistory.size();
                }
                for (int i = 0; i < size; i++) {
                    String lastPassword = passwordHistory.get(i).getPassword();
                    if (lastPassword.equals(newPassword)) {
                        throw new InvalidInputException(MessagesAndContent.USER_M023);
                    }
                }
            }

            currentUser.setPassword(newPassword);
            currentUser.setNoOfAttempts(0);
            currentUser.setFirstTime(false);

            userRepository.save(currentUser);

            updatePasswordHistory(currentUser);

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public void resetPassword(String epfNumber) throws CustomException {
        try {

            epfNumber = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M01, epfNumber);

            Optional<User> foundUser = userRepository.findById(epfNumber);

            if (!foundUser.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.USER_M09);
            }

            User currentUser = foundUser.get();

            if (currentUser.getStatus().equals(USER_STATUS_BLOCKED)) {
                throw new InvalidInputException(MessagesAndContent.USER_M018);
            }
            if (currentUser.getStatus().equals(USER_STATUS_INACTIVE)) {
                throw new InvalidInputException(MessagesAndContent.USER_SIGN_IN_M07);
            }

            String password = DataUtil.generatePassword();
            String encriptPassword = PasswordEncriptor.encriptPassword(password);
            currentUser.setPassword(encriptPassword);
            currentUser.setFirstTime(true);

            User savedUser = userRepository.save(currentUser);

            //sentEmail
            try {
                String messageBody = "Hey " + savedUser.getUserName() + ",\n"
                        + "\n"
                        + "You have recently requested a password reset for your iDoc account.\n"
                        + "To complete the process, provide the following temporary credentials provided and set a new password.\n"
                        + "\n"
                        + "Here are your credentials.\n"
                        + "\n"
                        + "Sign-in Details Username: Use your EPF number as the username. Temporary Password: " + password + " \n"
                        + "\n"
                        + "If you didn't make this change or if you believe an unauthorized person has accessed your account, please contact the system administrator."
                        + "\n"
                        + ndisUrl + "- iDOC Administration";

                emailService.send(savedUser.getEmail(), "Reset password email", messageBody, null);
            } catch (Exception ex) {
                log.error(ex.getMessage());
                //sent Email error
            }

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new UnknownException(ex.getMessage());
        }
    }

    private void updatePasswordHistory(User user) {

        long currentTime = Instant.now().getEpochSecond();
        UserPasswordHistory userPasswordHistory = new UserPasswordHistory();
        userPasswordHistory.setEpfNumber(user.getEpfNumber());
        userPasswordHistory.setCreatedDateTime(currentTime);
        userPasswordHistory.setPassword(user.getPassword());
        userPasswordHistoryRepository.save(userPasswordHistory);

    }

    private void updateHistory(String action, User user, String description) throws CustomException {

        String userName = DataUtil.getUserName();
        String performedUserFullName = getUserFullNameByUserName(userName);
        long currentTime = Instant.now().getEpochSecond();
        UserHistory userHistory = new UserHistory();
        userHistory.setAction(action);
        userHistory.setEpfNumber(user.getEpfNumber());
        userHistory.setUserName(user.getUserName());
        userHistory.setFirstName(user.getFirstName());
        userHistory.setLastName(user.getLastName());
        userHistory.setAddress(user.getAddress());
        userHistory.setDateOfBirth(user.getDateOfBirth());
        userHistory.setGender(user.getGender());
        userHistory.setStorageQuota(user.getStorageQuota());
        userHistory.setEmail(user.getEmail());
        userHistory.setMobileNumber(user.getMobileNumber());
        userHistory.setDescription(description);
        userHistory.setPerformedBy(performedUserFullName);
        userHistory.setPerformedDateTime(currentTime);
        userHistory.setStatus(user.getStatus());
        userHistoryRepository.save(userHistory);

    }

    @Override
    public List<NotificationDto> getNotificationList() throws CustomException {
        try {
            List<Notification> list = new ArrayList<>();
            List<NotificationDto> dtoList = new ArrayList<>();
            String userName = DataUtil.getUserName();
            list = sqlCustomQueryRepository.getMyNotificationList(userName);

            for (Notification item : list) {
                NotificationDto dtoItem = new NotificationDto();
                BeanUtils.copyProperties(item, dtoItem);
                dtoList.add(dtoItem);
            }
            return dtoList;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public long getNotificationCount() throws CustomException {
        try {
            long count = 0;
            String userName = DataUtil.getUserName();
            count = sqlCustomQueryRepository.getMyNotificationCount(userName);

            return count;

        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public void readNotification(Long notificationId) throws CustomException {
        try {
            if (notificationId == null || notificationId < 1) {
                throw new InvalidInputException(MessagesAndContent.NOTIFICATION_II_M1);
            }

            Optional<Notification> foundNotifications = notificationRepository.findById(notificationId);
            if (!foundNotifications.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.NOTIFICATION_DNE_M01);
            }
            Notification foundNotification = foundNotifications.get();
            String userName = DataUtil.getUserName();

            if (!foundNotification.getRecipient().equals(userName)) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M27);
            }

            foundNotification.setReadMessage(Boolean.TRUE);
            notificationRepository.save(foundNotification);

        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public List<User> getUserListByStatus(String status) throws CustomException {
        try {
            Status statusValue = null;

            String validatedStatus = InputValidatorUtil.validateStringProperty(USER_M035, status);

            try {
                statusValue = Status.valueOf(validatedStatus);
            } catch (Exception e) {
                throw new InvalidInputException(COMMON_M29);
            }

            List<User> foundUserList = userRepository.findAllByStatus(validatedStatus);

            return foundUserList;
        } catch (CustomException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UnknownException(e.getMessage());
        }
    }

    // cron: seconds / minutes / hours / day of the month / month / day of the week
    // Current cron: At 00:00
    // Check cron: https://crontab.guru/  |  https://crontab.cronhub.io/
    // test : @Scheduled(initialDelay = 1000L, fixedRate = 15000L)

    @Scheduled(cron = "0 0 0 * * *")
    protected void checkUserStatus() throws CustomException {
        List<User> userListByActiveStatus = userRepository.findAllByStatus(USER_STATUS_ACTIVE);

        log.info("Running the scheduler task to check user activity at: " + System.currentTimeMillis());

        if (userListByActiveStatus != null) {
            for (User foundUser : userListByActiveStatus) {

                long currentTime = Instant.now().getEpochSecond();

                long diff = TimeUnit.SECONDS.toDays(currentTime - foundUser.getLastSignIn());

                if (diff >= userInactivationDays) {
                    foundUser.setStatus(USER_ACTION_INACTIVE);

                    userRepository.save(foundUser);
                    updateHistory(USER_ACTION_INACTIVE, foundUser, "User deactivated due to inactivity for " + userInactivationDays + " days.");
                }
            }
        }
    }


}
