package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.Tag;
import com.iterminal.ndis.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITagRepository extends JpaRepository<Tag, String> {
    @Query("SELECT g from Tag g where g.id=?1")
    Optional<Tag> findById(long tag_Id);

    @Query("SELECT g from Tag g where g.name=?1")
    Optional<Tag> findByName(String name);

    int countTagsByNameEquals(String name);



}
