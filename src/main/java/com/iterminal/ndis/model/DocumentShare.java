package com.iterminal.ndis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "document_share")
public class DocumentShare {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", unique = true, nullable = false, length = 50)
    private Long Id;

    @Column(name = "document_id", length = 50, nullable = false)
    private Long documentId;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "sharing_link", length = 50, nullable = false)
    private String sharingLink;
}
