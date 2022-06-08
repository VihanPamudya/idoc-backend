package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.UserGroup;
import com.iterminal.ndis.model.UserGroupGroups;
import com.iterminal.ndis.model.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserGroupGroupsRepository extends JpaRepository<UserGroupGroups,Long> {
    public List<UserGroupGroups> findByEpfNumber(String epfNumber);

    @Query("SELECT count(g.id) from UserGroupGroups g where g.userGroup.id=?1")
    int countByUserGroup(Long group);
}
