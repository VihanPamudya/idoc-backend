package com.iterminal.ndis.service.impl;

import com.iterminal.exception.*;
import com.iterminal.ndis.dto.DocumentMetadataRequestDto;
import com.iterminal.ndis.dto.DocumentRequestDto;
import com.iterminal.ndis.dto.response.DocumentDto;
import com.iterminal.ndis.dto.response.DocumentMetadataDto;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.model.*;
import com.iterminal.ndis.repository.*;
import com.iterminal.ndis.service.IDocumentService;
import com.iterminal.ndis.util.*;
import com.iterminal.searchfilters.FilterOperator;
import com.iterminal.searchfilters.FilterUtil;
import com.iterminal.searchfilters.PropertyFilterDto;
import com.iterminal.searchfilters.RequestListDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class DocumentService implements IDocumentService {
    private final IDocumentRepository documentRepository;
    private final ITagRepository tagRepository;
    private final IDocumentTagsRepository documentTagsRepository;
    private final IDocumentHistoryRepository documentHistoryRepository;
    private final IDocumentShareRepository documentShareRepository;

    private final IFileRespository fileRespository;


    private final IUserRepository userRepository;

    private final CustomQueryRepository<Document> customQueryRepository;

    final String TABLE_DOCUMENT = "document";
    final String DOCUMENT_STATUS_ACTIVE = "Active";
    final String DOCUMENT_STATUS_INACTIVE = "Inactive";

    private final String DOCUMENT_ACTION_CREATE = "Create";
    private final String DOCUMENT_ACTION_INACTIVE = "Inactive";
    private final String DOCUMENT_ACTION_ACTIVE = "Active";
    private final String DOCUMENT_ACTION_UPDATE = "Update";

    @Autowired
    public DocumentService(IDocumentRepository documentRepository,
                           ITagRepository tagRepository,
                           IDocumentTagsRepository documentTagsRepository,
                           IDocumentHistoryRepository documentHistoryRepository,
                           IFileRespository fileRespository,
                           IUserRepository userRepository,
                           CustomQueryRepository customQueryRepository,
                           IDocumentShareRepository documentShareRepository) {
        this.documentRepository = documentRepository;
        this.tagRepository = tagRepository;
        this.documentTagsRepository = documentTagsRepository;
        this.userRepository = userRepository;
        this.customQueryRepository = customQueryRepository;
        this.fileRespository = fileRespository;
        this.documentHistoryRepository = documentHistoryRepository;
        this.documentShareRepository = documentShareRepository;
    }

    @Override
    public DocumentDto create(DocumentRequestDto request) throws CustomException {
        try {
            Document newDocumentRequest = new Document();

            if (request == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }

            String title = InputValidatorUtil.validateStringProperty(MessagesAndContent.DOCUMENT_M01, request.getTitle(), "Document Title", 50);

            Optional<Document> foundDocument = documentRepository.findByTitle(title);
            if (foundDocument.isPresent()) {
                throw new AlreadyExistException("A Document with name " + title + " already exists.");
            }
            newDocumentRequest.setTitle(title);

            newDocumentRequest.setCreatedDate(request.getCreatedDate());

            String description = InputValidatorUtil.validateStringProperty(MessagesAndContent.DOCUMENT_M02, request.getDescription(), "Document Description", 500);
            newDocumentRequest.setDescription(description);

            String language = InputValidatorUtil.validateStringProperty(MessagesAndContent.DOCUMENT_M03, request.getLanguage(), "Document Language", 50);
            newDocumentRequest.setLanguage(language);

            List<Tag> documentTagList = new ArrayList<>();

            newDocumentRequest.setCreatedBy(DataUtil.getUserName());

            newDocumentRequest.setStatus(DOCUMENT_STATUS_ACTIVE);

            Document savedDocument = documentRepository.save(newDocumentRequest);
            if (request.getTags() != null) {
                for (Tag tag : request.getTags()) {
                    Optional<Tag> foundTag = tagRepository.findById(tag.getId());
                    if (!foundTag.isPresent()) {
                        throw new DoesNotExistException(MessagesAndContent.TAG_M03);
                    }
                    DocumentTags newDocumentTag = new DocumentTags();
                    newDocumentTag.setDocumentId(savedDocument.getDocument_id());
                    newDocumentTag.setTag(tag);
                    documentTagsRepository.save(newDocumentTag);
                    documentTagList.add(tag);
                }
            }


            updateHistory(DOCUMENT_ACTION_CREATE, savedDocument, "Document created");


            DocumentDto documentDto = convertDocumentToDocumentResponseDto(savedDocument);
            documentDto.setTags(documentTagList);

            return documentDto;

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public List<DocumentDto> getAll() throws CustomException {

        try {
            List<DocumentDto> allDocuments = new ArrayList<>();
            List<Document> documents = documentRepository.findAll();

            for (Document document : documents) {
                List<Tag> documentTagList = new ArrayList<>();
                List<DocumentTags> tagList = documentTagsRepository.findAllByDocumentId(document.getDocument_id());
                if (tagList.size() != 0) {
                    for (DocumentTags docTag : tagList) {
                        Optional<Tag> foundTag = tagRepository.findById(docTag.getTag().getId());
                        if (foundTag.isPresent()) {
                            documentTagList.add(foundTag.get());
                        } else {
                            throw new DoesNotExistException(MessagesAndContent.TAG_M03);
                        }
                    }
                }
                DocumentDto documentDto = convertDocumentToDocumentResponseDto(document);
                documentDto.setTags(documentTagList);
                allDocuments.add(documentDto);
            }


            return allDocuments;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public PaginationDto<DocumentDto> getPaginatedList(RequestListDto requestList) throws CustomException {

        try {

            if (requestList == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }
            PaginationDto<DocumentDto> paginationDto = new PaginationDto<>();
            List<Document> list = new ArrayList<>();
            List<DocumentDto> documentList = new ArrayList<>();

            int page = requestList.getPage();
            int limit = requestList.getLimit();
            if (page <= 0) {
                page = 1;
            }
            if (limit <= 0) {
                limit = 20;
            }

            List<PropertyFilterDto> propertyFilterDtoList = requestList.getFilterData();
            PropertyFilterDto InactivePFD = new PropertyFilterDto();

            InactivePFD.setValue("'Active'");
            InactivePFD.setProperty("status");
            InactivePFD.setOperator(FilterOperator.EQUAL);

            propertyFilterDtoList.add(InactivePFD);

            String sql = FilterUtil.generateListSql(TABLE_DOCUMENT, propertyFilterDtoList, requestList.getOrderFields(), true, page, limit);
            list = customQueryRepository.getResultList(sql, Document.class);

            for (Document document : list) {
                List<Tag> documentTagList = new ArrayList<>();
                List<DocumentTags> tagList = documentTagsRepository.findAllByDocumentId(document.getDocument_id());
                if (tagList.size() != 0) {
                    for (DocumentTags docTag : tagList) {
                        Optional<Tag> foundTag = tagRepository.findById(docTag.getTag().getId());
                        if (foundTag.isPresent()) {
                            documentTagList.add(foundTag.get());
                        } else {
                            throw new DoesNotExistException(MessagesAndContent.TAG_M03);
                        }
                    }
                }
                DocumentDto documentDto = convertDocumentToDocumentResponseDto(document);
                documentDto.setTags(documentTagList);
                documentList.add(documentDto);
            }

            paginationDto.setData(documentList);
            paginationDto.setTotalSize(documentRepository.countDocumentByStatusEquals("Active"));
            return paginationDto;
        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            String err = "";
            if (ex.getCause() != null && ex.getCause().getCause() != null && ex.getCause().getCause().getMessage() != null) {
                err = ex.getCause().getCause().getMessage();
            }
            throw new InvalidFilterInputException("Sql query execute failed. " + err);
        }
    }

    @Override
    public DocumentDto update(long doc_id, DocumentRequestDto document) throws CustomException {

        try {
            if (document == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }

            Optional<Document> foundDocument = documentRepository.findById(doc_id);

            if (!foundDocument.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.DOCUMENT_M04);
            }

            Document currentDocument = foundDocument.get();

            String title = InputValidatorUtil.validateStringProperty(MessagesAndContent.DOCUMENT_M01, document.getTitle(), "Document Title", 50);
            int foundDoc = documentRepository.countDocumentsByTitleEquals(title);
            if (foundDoc > 0 && !foundDocument.get().getTitle().equals(document.getTitle())) {
                throw new AlreadyExistException("A document with name " + title + " already exists.");
            }

            currentDocument.setTitle(title);

            currentDocument.setCreatedDate(document.getCreatedDate());

            String description = InputValidatorUtil.validateStringProperty(MessagesAndContent.DOCUMENT_M02, document.getDescription(), "Document Description", 500);
            currentDocument.setDescription(description);

            String language = InputValidatorUtil.validateStringProperty(MessagesAndContent.DOCUMENT_M03, document.getLanguage(), "Document Language", 50);
            currentDocument.setLanguage(language);

            currentDocument.setCreatedBy(DataUtil.getUserName());

            List<Tag> documentTagList = new ArrayList<>();

            if (document.getTags().size() != 0) {
                List<DocumentTags> documentTagsList = documentTagsRepository.findAllByDocumentId(doc_id);
                documentTagsRepository.deleteAll(documentTagsList);


                for (Tag tag : document.getTags()) {
                    Optional<Tag> foundTag = tagRepository.findById(tag.getId());
                    if (!foundTag.isPresent()) {
                        throw new DoesNotExistException(MessagesAndContent.TAG_M03);
                    }
                    DocumentTags newDocumentTag = new DocumentTags();
                    newDocumentTag.setDocumentId(document.getDocument_id());
                    newDocumentTag.setTag(tag);
                    documentTagsRepository.save(newDocumentTag);
                    documentTagList.add(tag);

                }
            }

            Document updatedDocument = documentRepository.save(currentDocument);

            updateHistory(DOCUMENT_ACTION_UPDATE, updatedDocument, "Document updated");

            DocumentDto documentDto = convertDocumentToDocumentResponseDto(updatedDocument);
            documentDto.setTags(documentTagList);
            return documentDto;

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public void delete(long doc_id) throws CustomException {
        try {
            Optional<Document> foundDocuments = documentRepository.findById(doc_id);

            if (foundDocuments.isPresent()) {
                documentRepository.delete(foundDocuments.get());
            } else {
                throw new DoesNotExistException(MessagesAndContent.TAG_M03);
            }
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public Document inactive(long document_id) throws CustomException {

        try {
            Optional<Document> foundDocuments = documentRepository.findById(document_id);

            if (!foundDocuments.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.DOCUMENT_M04);
            }
            Document currentDocument = foundDocuments.get();
            if (currentDocument.getStatus().equals(DOCUMENT_STATUS_INACTIVE)) {
                throw new AlreadyExistException(MessagesAndContent.DOCUMENT_M05);
            }

            currentDocument.setStatus(DOCUMENT_STATUS_INACTIVE);
            Document updatedDocument = documentRepository.save(currentDocument);

            updateHistory(DOCUMENT_ACTION_INACTIVE, updatedDocument, "Document inactivated");

            return updatedDocument;

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public List<DocumentHistory> getDocumentHistory(long document_id) throws CustomException {
        try {

            String documentId = InputValidatorUtil.validateStringProperty(MessagesAndContent.DOCUMENT_M06, String.valueOf(document_id));
            Optional<Document> foundDocument = documentRepository.findById(document_id);

            if (!foundDocument.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.DOCUMENT_M04);
            }


            return documentHistoryRepository.findAllByDocumentId(document_id, Sort.by(Sort.Direction.DESC, "performedDateTime"));

        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    private void updateHistory(String action, Document document, String description) throws CustomException {

        long currentTime = Instant.now().getEpochSecond();
        DocumentHistory documentHistory = new DocumentHistory();
        documentHistory.setAction(action);
        documentHistory.setDocumentId(document.getDocument_id());
        documentHistory.setDescription(description);
        Optional<User> foundUser = userRepository.findById(document.getCreatedBy());
        documentHistory.setPerformedBy(foundUser.get().getFirstName() + " " + foundUser.get().getLastName());
        documentHistory.setPerformedDateTime(currentTime);
        documentHistory.setTitle(document.getTitle());
        documentHistory.setStatus(document.getStatus());
        documentHistoryRepository.save(documentHistory);

    }

    @Override
    public DocumentMetadataDto getMetaData(long document_id) throws CustomException {
        return null;
    }

    @Override
    public DocumentMetadataDto updateMetadata(DocumentMetadataRequestDto metaData) throws CustomException {
        return null;
    }

    @Override
    public DocumentDto convertDocumentToDocumentResponseDto(Document document) throws CustomException {
        DocumentDto documentDto = new DocumentDto();
        BeanUtils.copyProperties(document, documentDto);
        return documentDto;
    }

    @Override
    public DocumentMetadataDto convertDocumentMetadataToDocumentMetadataResponseDto(DocumentMetadata metaData) throws CustomException {
        DocumentMetadataDto documentMetadataDto = new DocumentMetadataDto();
        BeanUtils.copyProperties(metaData, documentMetadataDto);
        return documentMetadataDto;
    }

    @Override
    public String getSharingLink(Long documentId) throws CustomException {

        try {
            DocumentShare docShare = new DocumentShare();

            //check whether document exist

            Long time = DateTimeUtil.getCurrentTime();
            String user_id = DataUtil.getUserName();
            String docId = Long.toString(documentId);
            String currentTime = Long.toString(time);
            String hashed = HashUtil.generateHash(currentTime+docId+user_id);
            String sharingLink = "http://localhost:3000/share/"+ hashed;

            docShare.setDocumentId(documentId);
            docShare.setSharingLink(hashed);
            docShare.setUserId(user_id);

            return sharingLink;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }

    }

    @Override
    public void UnshareLink(Long documentId) throws CustomException {
        try {
            String user_id = DataUtil.getUserName();
            //check whether document exist
            documentShareRepository.deleteByDocumentIdAndUserId(documentId,user_id);

        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }





    }
}


