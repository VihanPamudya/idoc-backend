/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iterminal.ndis.dto;

import java.util.ArrayList;
import com.iterminal.ndis.model.Package;
import java.util.List;

/**
 *
 * @author suranga Created date: Feb 20, 2022
 */
public class PermissionDto {

    List<Package> packageList = new ArrayList<>();

    public List<Package> getPackageList() {
        return packageList;
    }

    public void setPackageList(List<Package> packageList) {
        this.packageList = packageList;
    }

}
