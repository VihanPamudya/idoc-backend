/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.util;

import com.iterminal.ndis.model.SubPackage;
import com.iterminal.ndis.model.SubPackageAction;
import com.iterminal.ndis.model.PackageAction;
import com.iterminal.ndis.model.ModuleAction;
import com.iterminal.ndis.model.SubModule;
import com.iterminal.ndis.model.SubModuleAction;
import com.iterminal.ndis.dto.PermissionDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.iterminal.ndis.model.Module;
import com.iterminal.ndis.model.Package;
import org.springframework.security.core.context.SecurityContextHolder;


public class DataUtil {

    public final static String STAR = "*";
    public static final String NOTIFICATION_TYPE_INFO = "Info";
    private final static String PACKAGE_KEYS = "system-configuration,user-management,my-profile";

    final static String SUB_PACKAGE_KEYS = "role-and-permission,user";

    final static String MODULE_KEYS = "";

    final static String SUB_MODULE_KEYS = "";

    final static String ACTION_KEYS = "add,modify,view,delete,"
            + "approve,return,cancel,active,inactive,change-password,reset-password,"
            + "block,unblock,lock,unlock,permissions";

    final static String SYSTEM_KEYS = PACKAGE_KEYS + "," + SUB_PACKAGE_KEYS + ","
            + MODULE_KEYS + "," + SUB_MODULE_KEYS + "," + ACTION_KEYS;

    public final static String COLON = ":";

    public static String generateFeatureAction(String url) {
        String featureAction = "";
        SYSTEM_KEYS.split(",");
        String str[] = SYSTEM_KEYS.split(",");
        List<String> keys = new ArrayList<String>();
        keys = Arrays.asList(str);
        if (url != null && url.length() > 1) {
            String[] data = url.split("/");
            if (data != null && data.length > 0) {
                for (String key : data) {
                    if (keys.contains(key)) {
                        if (featureAction.isEmpty()) {
                            featureAction = key;
                        } else {
                            featureAction = featureAction + COLON + key;
                        }
                    }
                }
            }
        }

        return featureAction;
    }

    public static String generatePassword() {
        String password = "";
        int min = 100000;
        int max = 999999;
        Random random = new Random();
        password = password + random.ints(min, max).findFirst().getAsInt();
        return password;
    }

    public static ArrayList<String> getFeatureActionList(PermissionDto permissionData) {

        ArrayList<String> featureActionList = new ArrayList<>();

        if (permissionData != null && permissionData.getPackageList() != null && permissionData.getPackageList().size() > 0) {

            for (Package packageData : permissionData.getPackageList()) {

                for (PackageAction packageAction : packageData.getActionList()) {
                    String featureAction = packageData.getName() + COLON + packageAction.getAction().getName();
                    featureActionList.add(featureAction);
                }

                for (SubPackage subPackage : packageData.getSubPackageList()) {

                    for (SubPackageAction subPackageAction : subPackage.getActionList()) {
                        String featureAction = packageData.getName() + COLON + subPackage.getName() + COLON + subPackageAction.getAction().getName();
                        featureActionList.add(featureAction);
                    }

                    for (Module module : subPackage.getModuleList()) {

                        for (ModuleAction moduleAction : module.getActionList()) {
                            String featureAction = packageData.getName() + COLON + subPackage.getName() + COLON + module.getName() + COLON + moduleAction.getAction().getName();
                            featureActionList.add(featureAction);
                        }

                        for (SubModule subModule : module.getSubModuleList()) {

                            for (SubModuleAction subModuleAction : subModule.getActionList()) {
                                String featureAction = packageData.getName() + COLON + subPackage.getName() + COLON + module.getName() + COLON + subModule.getName() + COLON + subModuleAction.getAction().getName();
                                featureActionList.add(featureAction);
                            }

                        }

                    }
                }

            }

        }

        return featureActionList;
    }

    public static String getUserName() {

        String userName = "sys-user";
        if (SecurityContextHolder.getContext().getAuthentication() != null && SecurityContextHolder.getContext().getAuthentication().getCredentials() != null) {
            userName = SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
        }

        return userName;

    }

    public static boolean validateOldNic(String nic) {
        boolean valid = false;
        if (nic != null && (nic.matches("^[0-9]{9}[vVxX]$"))) {
            valid = true;
        }
        return valid;
    }

    public static boolean validateNewNic(String nic) {
        boolean valid = false;
        if (nic != null && (nic.matches("^[0-9]{12}$"))) {
            valid = true;
        }
        return valid;
    }

    public static String oldNicToNewNic(String oldNic) {
        String newNic = "19" + oldNic.substring(0, 5) + "0" + oldNic.substring(5, 9);
        return newNic;
    }

}
