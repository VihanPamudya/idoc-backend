package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.Notification;
import com.iterminal.ndis.model.UserRole;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class SqlCustomQueryRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<UserRole> getUserRoleList(String epfNumber) {
        String sql = "select * from user_role where epf_number = '" + epfNumber + "' order by id";
        Query query = entityManager.createNativeQuery(sql, UserRole.class);
        List<UserRole> resultList = query.getResultList();
        return resultList;
    }

    public long getUserCountByRoleId(long roleId) {

        String sql = "select count(*) from user_role where role_id = " + roleId;
        Query query = entityManager.createNativeQuery(sql);
        Long count = Long.parseLong(query.getSingleResult().toString());
        return count;

    }


    public boolean isExitTitle(String name) {
        boolean isExit = false;
        String sql = "select count(*) from title where name = '" + name + "'";
        Query query = entityManager.createNativeQuery(sql);
        Long count = Long.parseLong(query.getSingleResult().toString());
        if (count > 0) {
            isExit = true;
        }
        return isExit;

    }

    public List<Notification> getMyNotificationList(String userName) {
        String sql = "select * from notification where recipient = '" + userName + "' order by send_date_time desc limit 20";
        Query query = entityManager.createNativeQuery(sql, Notification.class);
        List<Notification> resultList = query.getResultList();
        return resultList;
    }

    public long getMyNotificationCount(String userName) {
        String sql = "select count(*) from notification where recipient = '" + userName + "' and read_message=false";
        Query query = entityManager.createNativeQuery(sql);
        Long count = Long.parseLong(query.getSingleResult().toString());
        return count;
    }

    public List<String> getFeatureActionStatus(String userName) {
        String sql = "select fas.status from user u inner join user_role ur on u.epf_number = ur.epf_number  inner join role_feature_action rfa on rfa.role_id = ur.role_id  inner join feature_action_status fas on fas.feature_action_id = rfa.feature_action_id where u.epf_number = '" + userName + "' group by fas.status";
        Query query = entityManager.createNativeQuery(sql);
        List<String> resultList = query.getResultList();
        return resultList;

    }

    public boolean hasAllPermissions(String userName) {
        boolean isExit = false;
        String sql = "select count(*) from user_role ur inner join role r on ur.role_id = r.id where r.all_permissions = true and ur.epf_number = '" + userName + "'";
        Query query = entityManager.createNativeQuery(sql);
        Long count = Long.parseLong(query.getSingleResult().toString());
        if (count > 0) {
            isExit = true;
        }
        return isExit;

    }

    public boolean hasAdvanceAction(String userName) {
        boolean isExit = false;
        String sql = "select count(*) from  user_role ur inner join role_feature_action rfa on rfa.role_id = ur.role_id  inner join feature_action fa on fa.id = rfa.feature_action_id where ur.epf_number = '" + userName + "' and advance_action = true";
        Query query = entityManager.createNativeQuery(sql);
        Long count = Long.parseLong(query.getSingleResult().toString());
        if (count > 0) {
            isExit = true;
        }
        return isExit;
    }

    public List<Object[]> createNativeQuery(String sql) {
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> resultList = query.getResultList();
        return resultList;
    }

    public long getResultListCount(String sql) {

        Query query = entityManager.createNativeQuery(sql);
        Long count = Long.parseLong(query.getSingleResult().toString());
        return count;

    }

}
