package com.iterminal.ndis.model;


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
@Table(name = "file")
public class File {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "file_id", unique = true, nullable = false, length = 50)
    private Long fileId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name="file_path")
    private String filePath;

    @Column(name="file_type", nullable = false)
    private String fileType;

    @Column(name="created_date_time",nullable = false )
    private Long createdDateTime;

    @Column(name = "document_id", nullable = false,length = 50)
    private long document_id;

    @ManyToOne
    @JoinColumn(name = "createdBy", referencedColumnName = "epf_number")
    private User createdBy;

}
