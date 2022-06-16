package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.Document;
import com.iterminal.ndis.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IDocumentRepository extends JpaRepository<Document, String> {

    @Query("SELECT g from Document g where g.document_id=?1")
    Optional<Document> findById(long doc_id);

    @Query("SELECT g from Document g where g.title=?1")
    Optional<Document> findByTitle(String title);

    int countDocumentByStatusEquals(String status);

    int countDocumentsByTitleEquals(String title);

}
