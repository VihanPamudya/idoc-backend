package com.iterminal.ndis.controller;


import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.FileRequestDto;
import com.iterminal.ndis.dto.TagRequestDto;
import com.iterminal.ndis.dto.response.FileDto;
import com.iterminal.ndis.model.DocumentHistory;
import com.iterminal.ndis.model.File;
import com.iterminal.ndis.model.FileVersion;
import com.iterminal.ndis.service.IFileService;
import com.iterminal.ndis.util.ErrorResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("file-management/file")
public class FileController {
    private final IFileService fileService;

    @Autowired
    public FileController(IFileService fileService) {
        this.fileService = fileService;
    }


    @GetMapping("/view/list")
    public ResponseEntity<FileDto> getFile(@PathVariable("fileId") Long file_id) {
        try {
            FileDto fileDto = fileService.findByFileId(file_id);
            return new ResponseEntity<>(fileDto, HttpStatus.OK);
        } catch (CustomException e) {
            return ErrorResponseUtil.errorResponse(e);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/list/{document_id}")
    public ResponseEntity<List<File>> getAllFiles(@PathVariable("document_id") long document_id) {
        try {
            List<File> fileList = fileService.getAll(document_id);
            return new ResponseEntity<>(fileList, HttpStatus.OK);
        } catch (CustomException e) {
            return ErrorResponseUtil.errorResponse(e);
        }
    }

    @PostMapping(value = "/add/{document_id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileDto> addFile(@RequestParam("file") MultipartFile file,@PathVariable("document_id") long document_id) {
        try {
            FileDto addedFile = fileService.create(file,document_id);
            return new ResponseEntity<>(addedFile, HttpStatus.OK);
        } catch (CustomException e) {
            return ErrorResponseUtil.errorResponse(e);
        }
    }

    @PutMapping(value = "/update/{document_id}/{fileId}")
    public ResponseEntity<FileDto> updateFile(@PathVariable("fileId") long file_id, @PathVariable("file") MultipartFile file,@PathVariable("document_id") long document_id) {
        try {
            FileDto updatedFile = fileService.update(file_id, file,document_id);
            return new ResponseEntity<>(updatedFile, HttpStatus.OK);
        } catch (CustomException e) {
            return ErrorResponseUtil.errorResponse(e);
        }
    }

    @DeleteMapping(value = "/delete/{document_id}/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable("fileId") long file_id,@PathVariable("document_id") long document_id){
        try{
            fileService.delete(file_id,document_id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }catch(CustomException e){
            return ErrorResponseUtil.errorResponse(e);
        }
    }

    @GetMapping(value = "/{file_id}/history")
    public ResponseEntity<List<FileVersion>> getFileVersionHistory(@PathVariable("file_id") Long fileId) {

        try {
            List<FileVersion> fileVersion = fileService.getFileVersionHistory(fileId);
            return new ResponseEntity<>(fileVersion, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }

    }
}
