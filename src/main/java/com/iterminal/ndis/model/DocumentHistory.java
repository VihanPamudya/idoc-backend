package com.iterminal.ndis.model;

import javax.persistence.*;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "document_history")
public class DocumentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false, length = 50)
    private long documentId;

    @Column(name = "action", length = 20)
    private String action;

    @Column(name = "performed_by", length = 100)
    private String performedBy;

    @Column(name = "performed_date_time")
    private Long performedDateTime;

    @Column(name = "document_title", length = 50, nullable = false)
    private String title;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "description", length = 100)
    private String description;

}
