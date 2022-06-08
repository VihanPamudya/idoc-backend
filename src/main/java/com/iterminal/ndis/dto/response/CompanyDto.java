package com.iterminal.ndis.dto.response;

import com.iterminal.ndis.model.User;
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

public class CompanyDto {
    private Long companyId;
    private String companyName;
    private String companyEmail;
    private String companyAddress;
    private String totalStorage;
    private String documentUploadURL;
    private String contactName;
    private String contactEmail;
    private String contactNumber;
    private String ActiveUserCount;
    private String status;
    private Long createdDateTime;
    private String createdBy;

    private List<User> registerList = new ArrayList<>();
}
