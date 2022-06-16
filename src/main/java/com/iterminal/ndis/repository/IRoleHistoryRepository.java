package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.RoleHistory;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IRoleHistoryRepository extends JpaRepository<RoleHistory, String> {

    public List<RoleHistory> findByRoleId(Long id, Sort performedDateTime);

}
