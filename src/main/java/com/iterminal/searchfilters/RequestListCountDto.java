package com.iterminal.searchfilters;

import java.util.ArrayList;

public class RequestListCountDto {

    ArrayList<PropertyFilterDto> filterData = new ArrayList<>();

    public ArrayList<PropertyFilterDto> getFilterData() { return filterData;  }

    public void setFilterData(ArrayList<PropertyFilterDto> filterData) {
        this.filterData = filterData;
    }

}
