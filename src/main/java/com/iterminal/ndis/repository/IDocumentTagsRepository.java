package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.DocumentTags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDocumentTagsRepository extends JpaRepository<DocumentTags, Long> {

    public List<DocumentTags> findAllByDocumentId(Long doc_id);
}
