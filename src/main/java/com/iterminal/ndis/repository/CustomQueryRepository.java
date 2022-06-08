/*
 * Copyright (C) 2021 iTerminal Technologies Pvt Ltd.
 * All rights reserved. This software is the confidential and proprietary information of iTerminal Technologies Pvt Ltd.
 * You shall not disclose such confidential information and shall use it only in accordance with the terms of
 * the license agreement you entered into with iTerminal Technologies Pvt Ltd.
 */
package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.Notification;
import com.iterminal.ndis.model.Tag;
import com.iterminal.ndis.model.UserGroupGroups;
import com.iterminal.ndis.model.UserRole;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author suranga Created date: Sep 19, 2021
 * @param <T>
 */
@Repository
@Transactional(readOnly = true)
public class CustomQueryRepository<T> {

    @PersistenceContext
    EntityManager entityManager;

    public List<T> getResultList(String sql, Class type) {

        Query query = entityManager.createNativeQuery(sql, type);
        List<T> resultList = query.getResultList();
        return resultList;

    }

    public long getResultListCount(String sql) {

        Query query = entityManager.createNativeQuery(sql);
        Long count = Long.parseLong(query.getSingleResult().toString());
        return count;

    }

    public List<UserRole> getUserRoleList(String epfNumber) {
        String sql = "select * from user_role where epf_number = '" + epfNumber + "' order by id";
        Query query = entityManager.createNativeQuery(sql, UserRole.class);
        List<UserRole> resultList = query.getResultList();
        return resultList;

    }

    public List<Object[]> getUserDashboard(String userName) {

        String sql = "select status, count(*)  from application_file group by status";
        //String sql = "select * application_file where created_by ='" + epfNumber + "'";
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> resultList = query.getResultList();
        return resultList;

    }

    public long getUserCountByRoleId(long roleId) {

        String sql = "select count(*) from user_role where role_id = " + roleId;
        Query query = entityManager.createNativeQuery(sql);
        Long count = Long.parseLong(query.getSingleResult().toString());
        return count;

    }

    public List<Notification> getNotificationList(String sql) {

        Query query = entityManager.createNativeQuery(sql, Notification.class);
        List<Notification> resultList = query.getResultList();
        return resultList;

    }

}
