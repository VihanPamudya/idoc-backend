package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.FileVersion;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IFileVersionHistoryRepository extends JpaRepository<FileVersion,Long> {

    public List<FileVersion> findAllByFileId(Long fileId, Sort versionNo);

    @Query("SELECT count(d.versionNo) from FileVersion d where d.fileId=?1")
    int countFileVersionsByFileId(Long fileId);



}
