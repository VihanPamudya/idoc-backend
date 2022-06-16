package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.File;
import com.iterminal.ndis.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IFileRespository extends JpaRepository<File,String> {
    @Query("SELECT f from File f where f.fileId=?1")
    Optional<File> findByFileId(Long fileId);

    @Query("SELECT f from File f where f.fileName=?1")
    Optional<File> findFileByFileName(String fileName);

    @Query("SELECT f from File f where f.document_id=?1")
    List<File> findAllByDocument_id(long document_id);

    int countFilesByFileName(String fileName);

}
