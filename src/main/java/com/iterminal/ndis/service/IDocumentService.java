package com.iterminal.ndis.service;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.DocumentMetadataRequestDto;
import com.iterminal.ndis.dto.DocumentRequestDto;
import com.iterminal.ndis.dto.response.DocumentDto;
import com.iterminal.ndis.dto.response.DocumentMetadataDto;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.model.Document;
import com.iterminal.ndis.model.DocumentHistory;
import com.iterminal.ndis.model.DocumentMetadata;
import com.iterminal.searchfilters.RequestListDto;
import org.checkerframework.checker.units.qual.C;

import java.util.List;

public interface IDocumentService {

    public DocumentDto create(DocumentRequestDto request) throws CustomException;

    public List<DocumentDto> getAll() throws CustomException;

    public PaginationDto<DocumentDto> getPaginatedList(RequestListDto requestList) throws CustomException;

    public DocumentDto update(long doc_id, DocumentRequestDto document) throws CustomException;

    public Document inactive(long document_id) throws CustomException;

    public List<DocumentHistory> getDocumentHistory(long document_id) throws CustomException;

    public void delete(long doc_id) throws CustomException;

    public DocumentMetadataDto getMetaData(long document_id) throws CustomException;

    public DocumentMetadataDto updateMetadata(DocumentMetadataRequestDto metaData) throws CustomException;

    public DocumentDto convertDocumentToDocumentResponseDto(Document document) throws CustomException;

    public DocumentMetadataDto convertDocumentMetadataToDocumentMetadataResponseDto(DocumentMetadata metaData) throws CustomException;

    public String getSharingLink(Long documentId) throws CustomException;

    public void UnshareLink(Long documentId) throws CustomException;

}
