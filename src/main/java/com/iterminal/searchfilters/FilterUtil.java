package com.iterminal.searchfilters;

import com.iterminal.exception.CustomException;
import com.iterminal.exception.InvalidFilterInputException;
import com.iterminal.exception.InvalidInputException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author suranga Created date: Feb 20, 2022
 */
public class FilterUtil {

    private static String generatePropertyCondition(PropertyFilterDto propertyFilter) throws CustomException {

        try {

            String propertyClause = propertyFilter.getProperty();

            switch (propertyFilter.getOperator()) {

                case LIKE:
                    if (!(propertyFilter.getValue() == null || propertyFilter.getValue().toString().isEmpty())) {
                        propertyClause = " lower(" + propertyClause + ") like lower('%" + propertyFilter.getValue() + "%') ";
                    } else {
                        propertyClause = null;
                    }
                    break;
                case NOT_LIKE:
                    if (!(propertyFilter.getValue() == null || propertyFilter.getValue().toString().isEmpty())) {
                        propertyClause = " lower(" + propertyClause + ") not like lower('%" + propertyFilter.getValue() + "%') ";
                    } else {
                        propertyClause = null;
                    }
                    break;
                case STRING_EQUAL:
                    propertyClause += " = '" + propertyFilter.getValue() + "' ";
                    break;
                case STRING_NOT_EQUAL:
                    propertyClause += " <> '" + propertyFilter.getValue() + "' ";
                    break;
                case EQUAL:
                    propertyClause += " = " + propertyFilter.getValue() + " ";
                    break;
                case NOT_EQUAL:
                    propertyClause += " <> " + propertyFilter.getValue() + " ";
                    break;
                case LESS_THAN:
                    propertyClause += " < " + propertyFilter.getValue() + " ";
                    break;
                case GREATER_THAN:
                    propertyClause += " > " + propertyFilter.getValue() + " ";
                    break;
                case IS_NULL:
                    propertyClause += " < is null ";
                    break;
                case IS_NOT_NULL:
                    propertyClause += " > is not null ";
                    break;

            }

            return propertyClause;

        } catch (Exception ex) {
            throw new InvalidFilterInputException("Generating where condition failed.");
        }
    }

    public static String generateCondition(ArrayList<PropertyFilterDto> filterData) throws CustomException {
        try {
            StringBuilder buffer = new StringBuilder();

            String temp;
            boolean grouped;
            PropertyFilterDto f, g;
            FilterGroupingOperator go;

            ArrayList<PropertyFilterDto> usedFilters = new ArrayList<>();

            for (int i = 0; i < filterData.size(); i++) {
                f = filterData.get(i);
                temp = generatePropertyCondition(f);
                if (temp == null) {
                    continue;
                }
                grouped = false;
                if (usedFilters.contains(f)) {
                    continue;
                }

                if (f.getGroup() != null && !f.getGroup().isEmpty()) {

                    for (int j = i + 1; j < filterData.size(); j++) {
                        g = filterData.get(j);

                        if (f.getGroup().equals(g.getGroup())) {

                            grouped = true;
                            go = g.getGroupingOperator();
                            if (go == null) {
                                go = FilterGroupingOperator.AND;
                            }

                            usedFilters.add(g);

                            switch (go) {
                                case AND:
                                    temp += " and " + generatePropertyCondition(g);
                                    break;

                                case OR:
                                    temp += " or " + generatePropertyCondition(g);
                                    break;
                            }

                        }

                    }

                } else {

                    for (int j = i + 1; j < filterData.size(); j++) {
                        g = filterData.get(j);

                        if (f.getProperty() != null && f.getProperty().equals(g.getGroupProperty())) {
                            grouped = true;
                            go = g.getGroupingOperator();
                            if (go == null) {
                                go = FilterGroupingOperator.AND;
                            }
                            usedFilters.add(g);
                            switch (go) {
                                case AND:
                                    temp += " and " + generatePropertyCondition(g);
                                    break;

                                case OR:
                                    temp += " or " + generatePropertyCondition(g);
                                    break;
                            }

                        }
                    }
                }

                if (grouped) {
                    temp = "(" + temp + ") ";
                }

                if (temp != null && !temp.isEmpty()) {
                    if (f.getGroupingOperator() != null && buffer.chars().count() > 0) {
                        switch (f.getGroupingOperator()) {
                            case AND:
                                buffer.append(" and ");
                                break;

                            case OR:
                                buffer.append(" or ");
                                break;
                        }

                    } else {
                        if (buffer.chars().count() > 0) {
                            buffer.append(" and ");
                        }

                    }

                    buffer.append(temp);
                }
                usedFilters.add(f);
            }

            return buffer.toString();
        } catch (InvalidFilterInputException ex) {
            throw new InvalidInputException("Generating where condition failed.");
        }
    }

    public static String generateCountSql(String table, List<PropertyFilterDto> filterData) throws CustomException {

        String sql = "";
        String whereClause = "";
        try {

            sql = "select count(*) as count from " + table;

            if (filterData != null && filterData.size() > 0) {
                whereClause = FilterUtil.generateCondition((ArrayList<PropertyFilterDto>) filterData);
            }

            if (!whereClause.isEmpty()) {
                sql = sql + " where " + whereClause;
            }

        } catch (Exception ex) {
            throw new InvalidFilterInputException("Generating Count Sql failed.");
        }
        return sql;
    }

    public static String generateCountSqlWithInnerJoin(String table, String innerJoinTable, List<String> schemeList, List<PropertyFilterDto> filterData, Boolean addAndOperator) throws CustomException {

        String sql = "";
        String remainingQuery = "";
        try {

            sql = "SELECT COUNT(*) as count FROM " + table + " INNER JOIN " + innerJoinTable + " ON " + table + ".id = " + innerJoinTable + ".type_of_injury_id";

            sql = sql + " WHERE " + "(";
            String innerJoinQuery = "";

            for (int i=0; i<schemeList.size(); i++) {
                String repeatQuery = innerJoinTable + "." + "scheme_code=";
                repeatQuery = repeatQuery + "'" + schemeList.get(i) + "'";

                if (i != schemeList.size()-1) {
                    repeatQuery += " OR ";
                } else {
                    if (addAndOperator) {
                        repeatQuery += ") AND ";
                    } else {
                        repeatQuery += ")";
                    }
                }

                innerJoinQuery += repeatQuery;
            }

            sql = sql + innerJoinQuery;

            if (filterData != null && filterData.size() > 0) {
                remainingQuery = FilterUtil.generateCondition((ArrayList<PropertyFilterDto>) filterData);
            }

            sql = sql + remainingQuery;

        } catch (Exception ex) {
            throw new InvalidFilterInputException("Generating Count Sql failed.");
        }
        return sql;
    }

    public static String generateCountSql(String table, String value) {

        String sql = "select count(*) as count from " + table + " where name = '" + value + "'";

        return sql;
    }

    public static String generateListSql(String table, List<PropertyFilterDto> filterData, ArrayList<String> orderFields, boolean isDescending, int page, int limit) throws CustomException {

        String sql = "";
        String whereClause = "";
        try {

            sql = "select * from " + table;

            if (filterData != null && filterData.size() > 0) {
                whereClause = FilterUtil.generateCondition((ArrayList<PropertyFilterDto>) filterData);
            }

            if (!whereClause.isEmpty()) {
                sql = sql + " where " + whereClause;
            }

            if (orderFields != null && orderFields.size() > 0) {
                String orderStr = String.join(",", orderFields);
                sql = sql + " order by " + orderStr;
                if (isDescending) {
                    sql += " desc";
                }
            }

            sql = sql + " limit " + limit + " offset " + ((page - 1) * limit);

        } catch (Exception ex) {
            throw new InvalidFilterInputException("Generating List Sql failed.");
        }
        return sql;
    }

    public static String generateListSqlWithInnerJoin(String table, String innerJoinTable, List<String> schemeList, List<PropertyFilterDto> filterData, ArrayList<String> orderFields, boolean isDescending, int page, int limit, Boolean addAndOperator) throws CustomException {

        String sql = "";
        String remainingQuery = "";
        try {

            sql = "SELECT * FROM " + table + " INNER JOIN " + innerJoinTable + " ON " + table + ".id = " + innerJoinTable + ".type_of_injury_id";

            sql = sql + " where " + "(";
            String innerJoinQuery = "";

            for (int i=0; i<schemeList.size(); i++) {
                String repeatQuery = innerJoinTable + "." + "scheme_code=";
                repeatQuery = repeatQuery + "'" + schemeList.get(i) + "'";

                if (i != schemeList.size()-1) {
                    repeatQuery += " OR ";
                } else {
                    if (addAndOperator) {
                        repeatQuery += ") AND ";
                    } else {
                        repeatQuery += ")";
                    }
                }

                innerJoinQuery += repeatQuery;
            }

            sql = sql + innerJoinQuery;

            if (filterData != null && filterData.size() > 0) {
                remainingQuery = FilterUtil.generateCondition((ArrayList<PropertyFilterDto>) filterData);
            }

            sql = sql + remainingQuery;

            if (orderFields != null && orderFields.size() > 0) {
                String orderStr = String.join(",", orderFields);
                sql = sql + " order by " + orderStr;
                if (isDescending) {
                    sql += " desc";
                }
            }

            sql = sql + " limit " + limit + " offset " + ((page - 1) * limit);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InvalidFilterInputException("Generating List Sql failed.");
        }
        return sql;
    }

}
