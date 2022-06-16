package com.iterminal.ndis.dto;

import com.iterminal.ndis.model.Tag;
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
public class DocumentRequestDto {
    private Long document_id;
    private String title;
    private String description;
    private String language;
    private List<Tag> tags;
    private String createdDate;
}
