/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.service;

import com.recursive.edu.backend.controller.request.AuthRefreshRequest;
import com.recursive.edu.backend.controller.request.LoginRequest;
import com.recursive.edu.backend.controller.response.AuthRefreshResponse;
import com.recursive.edu.backend.controller.response.LoginResponse;
import com.recursive.edu.backend.model.user.UserDetails;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
public interface AuthService {
    LoginResponse login(LoginRequest loginRequest) throws Exception;
    AuthRefreshResponse refresh(AuthRefreshRequest authRefreshRequest) throws Exception;
    UserDetails getCurrentUser(String accessToken) throws Exception;
}
