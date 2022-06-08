package com.iterminal.ndis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {

    @Id
    @Column(name = "epf_number", unique = true, nullable = false, length = 50)
    private String epfNumber;

    @JsonIgnore
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @JsonIgnore
    @Column(name = "token", nullable = false, length = 500)
    private String token;

    @JsonIgnore
    @Column(name = "refresh_token", nullable = false, length = 100)
    private String refreshToken;

//    @Column(name = "organization_id")
//    private long organizationId;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "storage_quota", nullable = false, length = 15)
    private String storageQuota;

    @Column(name = "gender", nullable = false, length = 15)
    private String gender;

//    @Column(name = "user_groups", length = 30)
//    private String groups;

    @Column(name = "date_of_birth", nullable = false, length = 15)
    private String dateOfBirth;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "mobile_number", nullable = false, length = 15)
    private String mobileNumber;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "created_date_time", nullable = false)
    private Long createdDateTime;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "last_sign_in", nullable = false)
    private Long lastSignIn;

    @Column(name = "sign_in", nullable = false)
    private boolean signIn;

    @Column(name = "first_time", nullable = false)
    private boolean firstTime;

    @JsonIgnore
    @Column(name = "no_of_attempts", nullable = false)
    private int noOfAttempts;

    @JsonIgnore
    @Column(name = "locked", nullable = false)
    private boolean locked;

    @JsonIgnore
    @Column(name = "locked_date_time")
    private Long lockedDateTime;

    @JsonIgnore
    @Column(name = "locked_by", length = 50)
    private String lockedBy;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "epf_number", insertable = false, updatable = false)
    private List<UserRole> roleList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "epf_number", insertable = false, updatable = false)
    private List<UserGroupGroups> groupList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "organization_id", insertable = false, updatable = false)
    private Organization organization;
}
