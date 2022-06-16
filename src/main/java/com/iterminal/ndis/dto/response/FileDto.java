package com.iterminal.ndis.dto.response;


import com.iterminal.ndis.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileDto {
    private Long fileId;
    private String fileName;
    private  Long createdDateTime;
    private User createdBy;
}
