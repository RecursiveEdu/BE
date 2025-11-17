/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
@Data
@Builder
public class UserDetails {
    @JsonIgnore
    private Long id;
    private String uuid;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String mobile;
    private String countryCode;
    private String name;
    private Boolean emailVerified;
    private String role;
}
