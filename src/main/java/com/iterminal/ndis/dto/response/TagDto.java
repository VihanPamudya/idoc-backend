package com.iterminal.ndis.dto.response;

import com.iterminal.ndis.model.Tag;
import com.iterminal.ndis.model.TagPermission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagDto {
    private Long id;
    private String name;
    private String color;
    private Long parentTag_id;
    private List<TagPermission> permissionList = new ArrayList<>();
    private List<Tag> childTags = new ArrayList<>();
}
