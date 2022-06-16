package com.iterminal.ndis.dto;


import com.iterminal.ndis.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileRequestDto {
    private Long fileId;
    private String filePath;
    private MultipartFile file;
    private User createdBy;
    private Long createdDateTime;
}
