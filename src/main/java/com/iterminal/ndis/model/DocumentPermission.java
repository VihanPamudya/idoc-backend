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
@Table(name = "document_permission")
public class DocumentPermission {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "document_permission_id", unique = true, nullable = false, length = 50)
    private Long id;

    @Column(name = "document_id", length = 50, nullable = false)
    private Long document_id;

    @Column(name = "epf_number", length = 50 )
    private String epfNumber;

    @Column(name = "group_id", length = 50 )
    private Long group_id;

    @Column(name = "Can_Read" )
    private boolean canRead;

    @Column(name = "Can_Write" )
    private boolean canWrite;
}
