package com.iterminal.ndis.service;

import com.iterminal.exception.CustomException;
import com.iterminal.ndis.dto.TagRequestDto;
import com.iterminal.ndis.dto.response.PaginationDto;
import com.iterminal.ndis.dto.response.TagDto;
import com.iterminal.ndis.model.Tag;
import com.iterminal.searchfilters.RequestListDto;

import java.util.List;

public interface ITagService {

    public TagDto create(TagRequestDto tag) throws CustomException;

    public TagDto findTagById(long tag_Id) throws CustomException;

    public List<Tag> findChildrenById(long tag_Id) throws CustomException;

    public List<Tag> getAll() throws CustomException;

    public TagDto update(long tag_Id, TagRequestDto tag) throws CustomException;

    public void delete(long tag_Id) throws CustomException;

    public TagDto convertTagToTagResponseDto(Tag tag) throws CustomException;

}
