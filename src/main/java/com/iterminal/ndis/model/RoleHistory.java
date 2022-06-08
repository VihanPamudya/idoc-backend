/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
@Table(name = "role_history")
public class RoleHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "action", length = 20)
    private String action;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "performed_by", length = 100)
    private String performedBy;

    @Column(name = "performed_date_time")
    private Long performedDateTime;

    @Column(name = "description", length = 100)
    private String description;
}
