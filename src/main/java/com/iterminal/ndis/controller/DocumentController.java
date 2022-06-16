package com.iterminal.ndis.controller;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.DocumentRequestDto;
import com.iterminal.ndis.dto.UserGroupRequestDto;
import com.iterminal.ndis.dto.response.DocumentDto;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.dto.response.UserGroupDto;
import com.iterminal.ndis.model.Document;
import com.iterminal.ndis.model.DocumentHistory;
import com.iterminal.ndis.model.UserGroup;
import com.iterminal.ndis.model.UserHistory;
import com.iterminal.ndis.service.IDocumentService;
import com.iterminal.ndis.util.ErrorResponseUtil;
import com.iterminal.searchfilters.RequestListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/document-management/document")
public class DocumentController {
    private final IDocumentService documentService;
    
    @Autowired
    public DocumentController(IDocumentService documentService) { this.documentService = documentService; }

    @GetMapping(value = "/list")
    public ResponseEntity<List<DocumentDto>> getAllDocuments() {

        try {
            List<DocumentDto> documentList = documentService.getAll();
            return new ResponseEntity<>(documentList, HttpStatus.OK);

        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PostMapping(value = "/list")
    public ResponseEntity<PaginationDto<DocumentDto>> getDocuments(@RequestBody RequestListDto requestList) {
        try {
            PaginationDto<DocumentDto> documentList = documentService.getPaginatedList(requestList);
            return new ResponseEntity<>(documentList, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PostMapping(value = "/add")
    public ResponseEntity<DocumentDto> createDocument(@RequestBody DocumentRequestDto document) {

        try {
            DocumentDto savedDocument = documentService.create(document);
            return new ResponseEntity<>(savedDocument, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PutMapping(value = "/modify/{id}")
    public ResponseEntity<DocumentDto> updateDocument(@PathVariable("id") long document_id, @RequestBody DocumentRequestDto document) {
        try {
            DocumentDto updatedDocument = documentService.update(document_id, document);
            return new ResponseEntity<>(updatedDocument, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @DeleteMapping(value = "/delete/{id}/")
    public ResponseEntity<Void> deleteDocument(@PathVariable("id") long doc_id) {
        try {
            documentService.delete(doc_id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PutMapping(value = "/{id}/inactive")
    public ResponseEntity<Document> inactive(@PathVariable("id") long document_id) {

        try{
            Document inactivatedDocument  = documentService.inactive(document_id);
            return new ResponseEntity<Document>(inactivatedDocument, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @GetMapping(value = "/{id}/history")
    public ResponseEntity<List<DocumentHistory>> getDocumentHistory(@PathVariable("id") long document_id) {

        try {
            List<DocumentHistory> documentHistory = documentService.getDocumentHistory(document_id);
            return new ResponseEntity<>(documentHistory, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }

    @PostMapping(value = "/share/{doc_id}")
    public ResponseEntity<String> shareDocument(@PathVariable("doc_id") Long doc_id) {
       try {
           String sharingLink = documentService.getSharingLink(doc_id);
           return new ResponseEntity<>(sharingLink,HttpStatus.OK);
       } catch (CustomException ex) {
           return ErrorResponseUtil.errorResponse(ex);
       }
    }

    @PostMapping(value = "/unshare/{doc_id}")
    public ResponseEntity<String> unShareDocument(@PathVariable("doc_id") Long doc_id) {
        try {
            documentService.UnshareLink(doc_id);
            return new ResponseEntity<>("successfully unshared",HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }


}
