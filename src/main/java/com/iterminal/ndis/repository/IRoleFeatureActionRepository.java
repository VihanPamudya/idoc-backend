package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.RoleFeatureAction;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IRoleFeatureActionRepository extends JpaRepository<RoleFeatureAction, Long> {

    public List<RoleFeatureAction> findByRoleId(Long id);

}
