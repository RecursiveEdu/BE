/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.controller.request;

import lombok.Getter;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
@Getter
public class LoginRequest {
    private String email;
    private String password;
}
