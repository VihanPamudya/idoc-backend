/**
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms
 * of the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.iterminal.ndis.dto.PermissionDto;
import lombok.Data;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "all_permissions")
    private boolean allPermissions;

    @JsonIgnore
    @Column(name = "permissions", columnDefinition = "TEXT")
    private String permissionData;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "created_date_time")
    private Long createdDateTime;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @JsonIgnore
    @Column(name = "locked", nullable = false)
    private boolean locked;

    @JsonIgnore
    @Column(name = "locked_date_time")
    private Long lockedDateTime;

    @JsonIgnore
    @Column(name = "locked_by", length = 50)
    private String lockedBy;

    public PermissionDto getPermissions() {
        PermissionDto permissions = null;
        if (permissionData != null && !permissionData.isEmpty()) {
            permissions = new Gson().fromJson(permissionData, PermissionDto.class);
        }
        return permissions;
    }

    public void setPermissions(PermissionDto permission) {

        if (permission == null) {
            permissionData = "";
        } else {
            permissionData = new Gson().toJson(permission);
        }

    }

}
