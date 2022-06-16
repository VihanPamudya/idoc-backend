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
@Table(name = "document_metadata")
public class DocumentMetadata {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "metadata_id", unique = true, nullable = false, length = 50)
    private Long id;

    @Column(name = "subject", length = 100, nullable = false, unique = true)
    private String subject;

    @Column(name = "identifier", length = 100, nullable = false, unique = true)
    private String identifier;

    @Column(name = "publisher", length = 100, nullable = false, unique = true)
    private String publisher;

    @Column(name = "format", length = 100, nullable = false, unique = true)
    private String format;

    @Column(name = "source", length = 100, nullable = false, unique = true)
    private String source;

    @Column(name = "Type", length = 100, nullable = false, unique = true)
    private String type;

    @Column(name = "coverage", length = 100, nullable = false, unique = true)
    private String coverage;

    @Column(name = "rights", length = 100, nullable = false, unique = true)
    private String rights;

    @Column(name = "relations", length = 100, nullable = false, unique = true)
    private String relations;

    @OneToOne
    @JoinColumn(name = "document_id", referencedColumnName = "document_id")
    private Document document;

}
