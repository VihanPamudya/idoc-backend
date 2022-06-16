
package com.iterminal.ndis.model;

import lombok.Data;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sub_package_action")
public class SubPackageAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sub_package_id")
    private Long subPackageId;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private Action action;

    @Transient
    private Boolean isSelected = false;

}
