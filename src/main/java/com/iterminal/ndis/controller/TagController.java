package com.iterminal.ndis.controller;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.TagRequestDto;
import com.iterminal.ndis.dto.UserGroupRequestDto;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.dto.response.TagDto;
import com.iterminal.ndis.dto.response.UserGroupDto;
import com.iterminal.ndis.model.Tag;
import com.iterminal.ndis.model.UserGroup;
import com.iterminal.ndis.service.ITagService;
import com.iterminal.ndis.util.ErrorResponseUtil;
import com.iterminal.searchfilters.RequestListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("tag-management/tag")
public class TagController {
    private final ITagService tagService;

    @Autowired
    public TagController(ITagService tagService) { this.tagService = tagService; }

    @GetMapping("/view/{id}")
    public ResponseEntity<TagDto> getTag(@PathVariable("id") Long tag_Id) {
        try {
            TagDto tagDto = tagService.findTagById(tag_Id);
            return new ResponseEntity<>(tagDto, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/list")
    public ResponseEntity<List<Tag>> getAllTags() {

        try {

            List<Tag> tagList = tagService.getAll();
            return new ResponseEntity<>(tagList, HttpStatus.OK);

        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PostMapping(value = "/add")
    public ResponseEntity<TagDto> createGroup(@RequestBody TagRequestDto tag) {

        try {
            TagDto savedTag = tagService.create(tag);
            return new ResponseEntity<>(savedTag, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @PutMapping(value = "/modify/{id}")
    public ResponseEntity<TagDto> updateTag(@PathVariable("id") long tag_Id, @RequestBody TagRequestDto tag) {
        try {
            TagDto updatedTag = tagService.update(tag_Id, tag);
            return new ResponseEntity<>(updatedTag, HttpStatus.OK);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable("id") long tag_Id) {
        System.out.println("abc abc");
        try {
            tagService.delete(tag_Id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (CustomException ex) {
            return ErrorResponseUtil.errorResponse(ex);
        }
    }


}
