package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.DocumentHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IDocumentHistoryRepository extends JpaRepository<DocumentHistory, Long> {
    public List<DocumentHistory> findAllByDocumentId(long document_id, Sort performedDateTime);
}
