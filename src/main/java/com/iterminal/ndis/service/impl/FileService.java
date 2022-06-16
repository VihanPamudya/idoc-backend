package com.iterminal.ndis.service.impl;

import com.iterminal.exception.*;
import com.iterminal.ndis.dto.response.FileDto;
import com.iterminal.ndis.model.*;
import com.iterminal.ndis.repository.CustomQueryRepository;
import com.iterminal.ndis.repository.IFileRespository;
import com.iterminal.ndis.repository.IFileVersionHistoryRepository;
import com.iterminal.ndis.repository.IUserRepository;
import com.iterminal.ndis.service.IFileService;
import com.iterminal.ndis.util.DataUtil;
import com.iterminal.ndis.util.InputValidatorUtil;
import com.iterminal.ndis.util.MessagesAndContent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class FileService implements IFileService {
    private final IFileRespository fileRespository;
    private final IUserRepository userRepository;
    private final IFileVersionHistoryRepository fileVersionHistoryRepository;
    private final CustomQueryRepository<File> customQueryRepository;


    final String TABEL_FILE = "file";

    @Autowired
    public FileService(IFileRespository fileRespository,
                       CustomQueryRepository customQueryRepository, IUserRepository userRepository, IFileVersionHistoryRepository fileVersionHistoryRepository) {
        this.fileRespository = fileRespository;
        this.userRepository = userRepository;
        this.customQueryRepository = customQueryRepository;
        this.fileVersionHistoryRepository = fileVersionHistoryRepository;
    }

    @Override
    public FileDto create(MultipartFile request, long document_id) throws CustomException {
        try {
            File newFileRequest = new File();
            if (request == null) {

            }
            String fileName = InputValidatorUtil.validateStringProperty(MessagesAndContent.FILE_M01, request.getOriginalFilename(), "File Name", 100);
            Optional<File> foundFile = fileRespository.findFileByFileName(fileName);
            if (foundFile.isPresent()) {
                throw new AlreadyExistException("File (" + fileName + ") is already exist");
            }


            Files.createDirectories(Paths.get("/upload/" + document_id).toAbsolutePath());

            Path filePath = Paths.get("/upload/" + document_id, fileName).toAbsolutePath();

            try {
                byte[] content = request.getBytes();
                System.out.println(filePath);
                Files.write(filePath, content);
            } catch (IOException e) {
                e.printStackTrace();
            }

            newFileRequest.setFilePath(filePath.toString());
            newFileRequest.setFileName(fileName);
            newFileRequest.setDocument_id(document_id);

            newFileRequest.setFileType(fileName.substring(fileName.lastIndexOf(".") + 1));

            User user = userRepository.getById(DataUtil.getUserName());
            newFileRequest.setCreatedBy(user);

            long currentTime = Instant.now().getEpochSecond();
            newFileRequest.setCreatedDateTime(currentTime);

            File savedFile = fileRespository.save(newFileRequest);

            updatefileVersionHistory(1, savedFile);

            FileDto fileDto = convertFileToFileResponseDto(savedFile);

            return fileDto;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new UnknownException(e.getMessage());
        }
    }

    @Override
    public FileDto findByFileId(long fileId) throws CustomException {
        try {
            Optional<File> foundFiles = fileRespository.findByFileId(fileId);

            if (foundFiles.isPresent()) {
                File foundfile = foundFiles.get();
                FileDto fileDto = convertFileToFileResponseDto(foundfile);
                return fileDto;
            } else {
                throw new DoesNotExistException(MessagesAndContent.FILE_M02);
            }

        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<File> getAll(long document_id) throws CustomException {
        try {
            List<File> foundFiles = fileRespository.findAllByDocument_id(document_id);
            return foundFiles;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public FileDto update(long file_id, MultipartFile file, long document_id) throws CustomException {
        try {
            if (file == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }
            Optional<File> foundFile = fileRespository.findByFileId(file_id);
            if (!foundFile.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.FILE_M02);
            }

            File currentFile = foundFile.get();
            String filename = InputValidatorUtil.validateStringProperty(MessagesAndContent.FILE_M01, file.getOriginalFilename(), "file name", 100);

            int fileFound = fileRespository.countFilesByFileName(filename);
            if (fileFound > 0 && !foundFile.get().getFileName().equals(file.getOriginalFilename())) {
                throw new AlreadyExistException("A File with name " + filename + " already exists. Rename the file with its version number and try again.");
            }


            String baseFileName = filename.split("\\.")[0];
            String extension = filename.split("\\.")[1];

            int version_no = fileVersionHistoryRepository.countFileVersionsByFileId(file_id);



            Path filePath = Paths.get("/upload/" + document_id, baseFileName +"v"+ (++version_no) + "." + extension).toAbsolutePath();

            currentFile.setFileName(filename);

            try {
                byte[] content = file.getBytes();
                System.out.println(filePath);
                Files.write(filePath, content);
            } catch (IOException e) {
                e.printStackTrace();
            }


            currentFile.setFileName(filename);

            currentFile.setDocument_id(document_id);

            currentFile.setFileType(filename.substring(filename.lastIndexOf(".") + 1));

            User user = userRepository.getById(DataUtil.getUserName());
            currentFile.setCreatedBy(user);

            currentFile.setFilePath(filePath.toString());

            updatefileVersionHistory(version_no, currentFile);

            File updatedFile = fileRespository.save(currentFile);

            FileDto fileDto = convertFileToFileResponseDto(updatedFile);
            return fileDto;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new UnknownException(e.getMessage());
        }
    }


    @Override
    public void delete(long file_id, long document_id) throws CustomException {
        try {
            Optional<File> foundFiles = fileRespository.findByFileId(file_id);

            if (foundFiles.isPresent()) {
                fileRespository.delete(foundFiles.get());

            } else {
                throw new DoesNotExistException(MessagesAndContent.FILE_M02);
            }

        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    private void updatefileVersionHistory(int versionNo, File file) throws CustomException {

        long currentTime = Instant.now().getEpochSecond();
        FileVersion fileVersion = new FileVersion();
        fileVersion.setVersionNo(versionNo);
        fileVersion.setFileId(file.getFileId());
        fileVersion.setFileName(file.getFileName());
        fileVersion.setFilePath(file.getFilePath());
        fileVersion.setCreatedDateTime(currentTime);
        fileVersion.setFileType(file.getFileType());
        fileVersionHistoryRepository.save(fileVersion);

    }

    @Override
    public List<FileVersion> getFileVersionHistory(Long fileId) throws CustomException {
        try {

            String file_id = InputValidatorUtil.validateStringProperty(MessagesAndContent.USER_M01, String.valueOf(fileId));
            Optional<File> foundFile = fileRespository.findByFileId(fileId);

            if (!foundFile.isPresent()) {
                throw new DoesNotExistException("File Id is required");
            }


            return fileVersionHistoryRepository.findAllByFileId(fileId, Sort.by(Sort.Direction.DESC, "versionNo"));

        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public FileDto convertFileToFileResponseDto(File file) throws CustomException {
        FileDto fileDto = new FileDto();
        BeanUtils.copyProperties(file, fileDto);
        return fileDto;
    }
}
