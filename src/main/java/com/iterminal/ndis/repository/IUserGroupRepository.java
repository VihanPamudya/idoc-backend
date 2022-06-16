package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.Tag;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserGroupRepository extends JpaRepository<UserGroup, Long> {

    @Query("SELECT g from UserGroup g where g.id=?1")
    Optional<UserGroup> findById(long group_Id);

    List<UserGroup> findAllByStatusEquals(String status);

    @Query("SELECT g from UserGroup g where g.name=?1")
    Optional<UserGroup> findByName(String name);

    int countUserGroupsByStatusEquals(String status);

    @Query("SELECT g.name from UserGroup g where g.id=?1")
    String getParentName(Long id);

    int countUserGroupsByNameEquals(String name);

}
