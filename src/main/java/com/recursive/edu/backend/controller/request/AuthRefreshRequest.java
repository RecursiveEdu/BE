/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.controller.request;

import lombok.Data;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
@Data
public class AuthRefreshRequest {
    private String refreshToken;
}
