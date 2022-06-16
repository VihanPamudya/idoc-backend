
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
@Table(name = "user_history")
public class UserHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "epf_number", nullable = false, length = 50)
    private String epfNumber;

    @Column(name = "action", length = 20)
    private String action;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "email", nullable = false, length = 50)
    private String email;

    @Column(name = "storage_quota", nullable = false, length = 15)
    private String storageQuota;

    @Column(name = "gender", nullable = false, length = 15)
    private String gender;

    @Column(name = "date_of_birth", nullable = false, length = 15)
    private String dateOfBirth;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "mobile_number", nullable = false, length = 15)
    private String mobileNumber;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "performed_by", length = 100)
    private String performedBy;

    @Column(name = "performed_date_time")
    private Long performedDateTime;

    @Column(name = "description", length = 100)
    private String description;

}
