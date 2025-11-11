package com.recursive.edu.backend.model.postgres;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "password")
    private String password;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "role")
    private String role;

    @Column(name = "country_code")
    private String countryCode;

    @Column(updatable = false, name = "created_dtm")
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "updated_dtm")
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name = "is_active")
    private Boolean active;

    @Column(name = "deleted_dtm")
    private Date deletedAt;

    @Column(name = "email_verified")
    private Boolean emailVerified;

}
