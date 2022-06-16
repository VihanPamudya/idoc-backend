package com.iterminal.ndis.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file_version_history")
public class FileVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long version_id;

    @Column(name = "file_id", nullable = false, length = 50)
    private long fileId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name="file_type" )
    private String fileType;

    @Column(name = "created_date_time", nullable = false)
    private Long createdDateTime;

    @Column(name = "file_path")
    private String filePath;

    @Column(name="version_no", nullable = false)
    private int versionNo;

}
