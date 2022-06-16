package com.iterminal.ndis.service;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.FileRequestDto;
import com.iterminal.ndis.dto.TagRequestDto;
import com.iterminal.ndis.dto.response.FileDto;
import com.iterminal.ndis.dto.response.TagDto;
import com.iterminal.ndis.dto.response.UserDto;
import com.iterminal.ndis.model.File;
import com.iterminal.ndis.model.FileVersion;
import com.iterminal.ndis.model.Tag;
import com.iterminal.ndis.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFileService {

    public FileDto create(MultipartFile file, long document_id) throws CustomException;

    public FileDto findByFileId(long fileId) throws CustomException;

    public List<File> getAll(long document_id) throws CustomException;

    public FileDto update(long fileId, MultipartFile file,long document_id) throws CustomException;

    public void delete(long fileId,long document_id) throws CustomException;

    public List<FileVersion> getFileVersionHistory(Long fileId) throws CustomException;

    public FileDto convertFileToFileResponseDto(File file) throws CustomException;
}
