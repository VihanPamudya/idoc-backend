package com.iterminal.ndis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "company")
public class Company {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "company_id", unique = true, nullable = false, length = 50)
    private Long companyId;

    @Column(name = "company_name", nullable = false, length = 100)
    private String companyName;

    @Column(name = "company_email", nullable = false, length = 100)
    private String companyEmail;

    @Column(name = "company_address", nullable = false, length = 100)
    private String companyAddress;

    @Column(name = "total_storage", nullable = false, length = 50)
    private String totalStorage;

    @Column(name = "document_upload_url", nullable = false, length = 100)
    private String documentUploadURL;

    @Column(name = "contatct_person_name", nullable = false, length = 100)
    private String contactName;

    @Column(name = "contatct_person_email", nullable = false, length = 100)
    private String contactEmail;

    @Column(name = "contatct_person_mobile_no", nullable = false, length = 100)
    private String contactNumber;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "created_date_time", nullable = false)
    private Long createdDateTime;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", insertable = false, updatable = false)
    private List<User> registerList = new ArrayList<>();
}
