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
@Table(name = "user_group_groups")
public class UserGroupGroups {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "epf_number", nullable = false, length = 50)
    private String epfNumber;

    @ManyToOne
    @JoinColumn(name = "g_id")
    private UserGroup userGroup;
}
