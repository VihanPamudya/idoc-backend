package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.Document;
import com.iterminal.ndis.model.DocumentShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface IDocumentShareRepository extends JpaRepository<DocumentShare, String> {

    void deleteByDocumentIdAndUserId(Long doc_id, String user_id);
}
