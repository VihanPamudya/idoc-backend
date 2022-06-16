package com.iterminal.ndis.dto;
 
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowPermissionRequestDto {
    private String permissionId;
    private String id;
    private String name;
    private boolean canRead;
    private boolean canWrite;
    private boolean isUser;
}
