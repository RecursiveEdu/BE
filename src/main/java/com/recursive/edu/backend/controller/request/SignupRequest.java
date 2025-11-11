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
public class SignupRequest {
    private String token;
    private UserSignupRequest userSignupRequest;
}
