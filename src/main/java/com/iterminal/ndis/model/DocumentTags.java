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
@Table(name = "document_tags")
public class DocumentTags {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "id", unique = true, nullable = false, length = 50)
    private Long id;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @OneToOne
    @JoinColumn(name = "tag_id", nullable = false, referencedColumnName = "Tag_id")
    private Tag tag;


}
