/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.controller.response;

import com.recursive.edu.backend.model.user.UserDetails;
import lombok.Builder;
import lombok.Data;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
@Data
@Builder
public class AuthRefreshResponse {
    private String accessToken;
    private UserDetails user;
}
