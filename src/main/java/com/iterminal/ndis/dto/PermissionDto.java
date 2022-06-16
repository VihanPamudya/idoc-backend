package com.iterminal.ndis.dto;

import java.util.ArrayList;
import com.iterminal.ndis.model.Package;
import java.util.List;


public class PermissionDto {

    List<Package> packageList = new ArrayList<>();

    public List<Package> getPackageList() {
        return packageList;
    }

    public void setPackageList(List<Package> packageList) {
        this.packageList = packageList;
    }

}
