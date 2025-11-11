/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.controller.response;

import com.recursive.edu.backend.model.user.UserDetails;
import lombok.*;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private UserDetails user;
}
