package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.Role;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

    public Optional<Role> findByName(String name);

    List<Role> findAllByLocked(boolean isLocked);

    List<Role> findAllByStatusEquals(String status, Sort createdDateTime);

}
