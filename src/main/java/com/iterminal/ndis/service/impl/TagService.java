package com.iterminal.ndis.service.impl;

import com.iterminal.exception.*;
import com.iterminal.ndis.dto.TagRequestDto;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.dto.response.TagDto;
import com.iterminal.ndis.dto.response.UserGroupDto;
import com.iterminal.ndis.model.Tag;
import com.iterminal.ndis.model.TagPermission;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.model.UserGroup;
import com.iterminal.ndis.repository.*;
import com.iterminal.ndis.service.ITagService;
import com.iterminal.ndis.service.IUserGroupService;
import com.iterminal.ndis.util.InputValidatorUtil;
import com.iterminal.ndis.util.MessagesAndContent;
import com.iterminal.searchfilters.FilterUtil;
import com.iterminal.searchfilters.RequestListDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TagService implements ITagService {
    private final ITagRepository tagRepository;
    private final CustomQueryRepository<Tag> customQueryRepository;
    private final IUserRepository userRepository;
    private final ITagPermissionRepository tagPermissionRepository;
    private final IUserGroupRepository userGroupRepository;

    final String TABLE_TAG = "tag";

    @Autowired
    public TagService(ITagRepository tagRepository,
                      CustomQueryRepository customQueryRepository,
                      IUserRepository userRepository,
                      ITagPermissionRepository tagPermissionRepository,
                      IUserGroupRepository userGroupRepository) {
        this.tagRepository = tagRepository;
        this.customQueryRepository = customQueryRepository;
        this.userRepository = userRepository;
        this.tagPermissionRepository = tagPermissionRepository;
        this.userGroupRepository = userGroupRepository;
    }

    @Override
    public TagDto create(TagRequestDto request) throws CustomException {
        try {
            Tag newTagRequest = new Tag();

            if(request == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }

            String name = InputValidatorUtil.validateStringProperty(MessagesAndContent.TAG_M01, request.getName(), "Tag Name", 100);

            Optional<Tag> foundTag = tagRepository.findByName(name);
            if(foundTag.isPresent()) {
                throw new AlreadyExistException("A tag with name " + name + " already exists.");
            }
            newTagRequest.setName(name);

            newTagRequest.setColor(request.getColor());

            long parentTag_Id = request.getParentTag_id();

            if( parentTag_Id != 0) {
                findTagById(parentTag_Id);
                newTagRequest.setParentTag_id(parentTag_Id);
            }
            else {
                newTagRequest.setParentTag_id(0L);
            }

            Tag savedTag = tagRepository.save(newTagRequest);

            List<TagPermission> savePermissionList = new ArrayList<>();
            for (TagPermission permission : request.getPermissionList()) {

               if(!permission.getEpfNumber().isEmpty()) {

                   String epfNumber = permission.getEpfNumber();
                   Optional<User> foundUsers = userRepository.findById(epfNumber);
                   if (!foundUsers.isPresent()) {
                       throw new DoesNotExistException(MessagesAndContent.USER_M09);
                   }

               } else {
                   Long group_id = permission.getGroup_id();
                   Optional<UserGroup> foundUserGroup = userGroupRepository.findById(group_id);
                   if (!foundUserGroup.isPresent()) {
                       throw new DoesNotExistException(MessagesAndContent.GROUP_M03);
                   }
               }
                permission.setTag_id(savedTag.getId());
                TagPermission tagPermission = tagPermissionRepository.save(permission);
                savePermissionList.add(tagPermission);
            }


            TagDto tagDto = convertTagToTagResponseDto(savedTag);
            tagDto.setPermissionList(savePermissionList);

            return tagDto;

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public TagDto findTagById(long tag_Id) throws CustomException {
        try {
            Optional<Tag> foundTags = tagRepository.findById(tag_Id);

            if(foundTags.isPresent()) {
                Tag foundTag = foundTags.get();
                TagDto tagDto = convertTagToTagResponseDto(foundTag);
                return tagDto;
            } else {
                throw new DoesNotExistException(MessagesAndContent.TAG_M03);
            }

        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<Tag> getAll() throws CustomException {
        try {
            List<Tag> foundTags =  tagRepository.findAll();
            List<Tag> parentTags = new ArrayList<>();
            for(Tag tag : foundTags) {

                if(tag.getParentTag_id().compareTo(0L) == 0) {
                  parentTags.add(tag);
                }
            }
            return parentTags;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public TagDto update(long tag_Id, TagRequestDto tag) throws CustomException {
        try {
            if(tag == null) {
                throw new InvalidInputException(MessagesAndContent.COMMON_M01);
            }

            Optional<Tag> foundTag = tagRepository.findById(tag_Id);

            if (!foundTag.isPresent()) {
                throw new DoesNotExistException(MessagesAndContent.TAG_M03);
            }

            Tag currentTag = foundTag.get();

            String name = InputValidatorUtil.validateStringProperty(MessagesAndContent.TAG_M01, tag.getName(), "tag name", 100 );
            int tagFound = tagRepository.countTagsByNameEquals(name);
            if(tagFound > 0 && !foundTag.get().getName().equals(tag.getName())) {
                throw new AlreadyExistException("A tag with name " + name + " already exists.");
            }

            long parentTag_Id = tag.getParentTag_id();

            if( parentTag_Id != 0) {
                findTagById(parentTag_Id);
                currentTag.setParentTag_id(parentTag_Id);
            }

            currentTag.setName(name);
            currentTag.setColor(tag.getColor());

            Tag updatedTag = tagRepository.save(currentTag);

            List<TagPermission> savePermissionList = new ArrayList<>();
            for (TagPermission permission : tag.getPermissionList()) {

                    if(!permission.getEpfNumber().isEmpty()) {
                        permission.setGroup_id(null);
                        String user_id = permission.getEpfNumber();
                        Optional<User> foundUsers = userRepository.findById(user_id);
                        if (!foundUsers.isPresent()) {
                            throw new DoesNotExistException(MessagesAndContent.USER_M09);
                        }
                    } else {
                        permission.setEpfNumber("");
                        Long group_id = permission.getGroup_id();
                        Optional<UserGroup> foundUserGroup = userGroupRepository.findById(group_id);
                        if (!foundUserGroup.isPresent()) {
                            throw new DoesNotExistException(MessagesAndContent.GROUP_M03);
                        }

                    }

                    permission.setTag_id(updatedTag.getId());
                    TagPermission tagPermission = tagPermissionRepository.save(permission);
                    savePermissionList.add(tagPermission);

            }

            TagDto tagDto = convertTagToTagResponseDto(updatedTag);
            tagDto.setPermissionList(savePermissionList);

            return tagDto;

        } catch (CustomException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public void delete(long tag_Id) throws CustomException {
        try {
            Optional<Tag> foundGroups = tagRepository.findById(tag_Id);

            if(foundGroups.isPresent()) {
                tagRepository.delete(foundGroups.get());
            } else {
                throw new DoesNotExistException(MessagesAndContent.TAG_M03);
            }

        } catch (Exception ex) {
            throw new UnknownException(ex.getMessage());
        }
    }

    @Override
    public TagDto convertTagToTagResponseDto(Tag tag) throws CustomException {
        TagDto tagDto = new TagDto();
        BeanUtils.copyProperties(tag, tagDto);
        return tagDto;
    }
}
