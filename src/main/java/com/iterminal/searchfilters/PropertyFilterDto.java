package com.iterminal.searchfilters;


public class PropertyFilterDto {

    String property = null;
    Object value = null;
    FilterOperator operator = null;
    String groupProperty = null;
    FilterGroupingOperator groupingOperator = null;
    String group = null;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public FilterOperator getOperator() {
        return operator;
    }

    public void setOperator(FilterOperator operator) {
        this.operator = operator;
    }

    public String getGroupProperty() {
        return groupProperty;
    }

    public void setGroupProperty(String groupProperty) {
        this.groupProperty = groupProperty;
    }

    public FilterGroupingOperator getGroupingOperator() {
        return groupingOperator;
    }

    public void setGroupingOperator(FilterGroupingOperator groupingOperator) {
        this.groupingOperator = groupingOperator;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
