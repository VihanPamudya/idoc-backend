package com.iterminal.searchfilters;

import java.util.ArrayList;


public class RequestListDto {

    int page;
    int limit;
    boolean descending;
    ArrayList<PropertyFilterDto> filterData = new ArrayList<>();
    ArrayList<String> orderFields = new ArrayList<>();

    public ArrayList<PropertyFilterDto> getFilterData() {
        return filterData;
    }

    public void setFilterData(ArrayList<PropertyFilterDto> filterData) {
        this.filterData = filterData;
    }

    public ArrayList<String> getOrderFields() {
        return orderFields;
    }

    public void setOrderFields(ArrayList<String> orderFields) {
        this.orderFields = orderFields;
    }

    public boolean isDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPage() {
        return page;
    }

}
