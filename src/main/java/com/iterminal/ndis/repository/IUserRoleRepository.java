package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IUserRoleRepository extends JpaRepository<UserRole, Long> {

    public List<UserRole> findByEpfNumber(String epfNumber);

}
