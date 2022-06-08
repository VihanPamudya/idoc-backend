package com.iterminal.ndis.dto.response;

import com.iterminal.ndis.model.UserGroup;
import com.iterminal.ndis.model.UserGroupGroups;
import com.iterminal.ndis.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private String epfNumber;
//    private Long organizationId;
//    private String organization;
    private String userName;
    private String firstName;
    private String lastName;
    private String password;
//    private String groups;
    private String storageQuota;
    private String gender;
    private String dateOfBirth;
    private String address;
    private String email;
    private String mobileNumber;
    private String status;
    private Long createdDateTime;
    private String createdBy;
    private Long lastSignIn;
    private boolean signIn;
    private boolean firstTime;
    private List<UserRole> roleList = new ArrayList<>();
    private List<UserGroupGroups> groupList = new ArrayList<>();
}
