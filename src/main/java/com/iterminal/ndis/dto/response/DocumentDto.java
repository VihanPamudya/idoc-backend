package com.iterminal.ndis.dto.response;

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
public class DocumentDto {
    private Long document_id;
    private String title;
    private String description;
    private String language;
    private List<Tag> tags;
    private String createdDate;
}
