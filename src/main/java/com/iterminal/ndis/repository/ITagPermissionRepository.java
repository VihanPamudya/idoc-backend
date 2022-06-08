package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.Tag;
import com.iterminal.ndis.model.TagPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ITagPermissionRepository extends JpaRepository<TagPermission, String> {

    TagPermission findById(long tag_Id);

}
