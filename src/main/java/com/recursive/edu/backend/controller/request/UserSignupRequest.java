/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.controller.request;

import lombok.Data;

/**
 * @author PrantikGuha
 * CreatedAt: {06-11-2025}
 */
@Data
public class UserSignupRequest {
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String mobile;
    private String password;
    private String countryCode;
}
