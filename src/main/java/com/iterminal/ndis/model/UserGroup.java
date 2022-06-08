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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_group")
public class UserGroup {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Group_Id", unique = true, nullable = false, length = 50)
    private Long id;

    @Column(name = "group_name", length = 50, nullable = false)
    private String name;

    @Column(name = "created_date_time", nullable = false)
    private Long createdDateTime;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @JoinColumn(name = "parent_group_id")
    private Long parentGroup_id;

    @Column(name = "status", length = 20, nullable = false)
    private String status;

    @Column(name = "member_count")
    private int member_count;
}
