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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tag")
public class Tag {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "Tag_Id", unique = true, nullable = false, length = 50)
    private Long id;

    @Column(name = "tag_name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "Color", length = 100, nullable = false)
    private String color;

    @Column(name = "parent_tag")
    private Long parentTag_id;

    @OneToMany
    @JoinColumn(name = "parent_tag")
    private List<Tag> childTags = new ArrayList<>();


}
