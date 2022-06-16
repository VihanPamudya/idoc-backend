package com.iterminal.ndis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentMetadataRequestDto {
    private Long id;
    private String subject;
    private String identifier;
    private String publisher;
    private String format;
    private String source;
    private String type;
    private String coverage;
    private String rights;
    private String relations;
}
