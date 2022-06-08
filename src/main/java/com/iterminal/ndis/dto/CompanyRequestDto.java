package com.iterminal.ndis.dto;

import com.iterminal.ndis.model.User;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompanyRequestDto {
    private Long companyId;
    private String companyName;
    private String companyEmail;
    private String companyAddress;
    private String totalStorage;
    private String documentUploadURL;
    private String contactName;
    private String contactEmail;
    private String contactNumber;
    private String status;
    private Long createdDateTime;
    private String createdBy;
    private List<User> registerList = new ArrayList<>();
}
