/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.service;

import com.recursive.edu.backend.controller.request.AuthRefreshRequest;
import com.recursive.edu.backend.controller.request.LoginRequest;
import com.recursive.edu.backend.controller.request.OtpValidationRequest;
import com.recursive.edu.backend.controller.request.SignupRequest;
import com.recursive.edu.backend.controller.response.AuthRefreshResponse;
import com.recursive.edu.backend.controller.response.LoginResponse;
import com.recursive.edu.backend.controller.response.OtpValidationResponse;
import com.recursive.edu.backend.controller.response.SignupResponse;
import com.recursive.edu.backend.model.exception.ApplicationException;
import com.recursive.edu.backend.model.user.UserDetails;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
public interface AuthService {
    LoginResponse login(LoginRequest loginRequest) throws Exception;
    AuthRefreshResponse refresh(AuthRefreshRequest authRefreshRequest) throws Exception;
    UserDetails getCurrentUser(String accessToken) throws Exception;
    SignupResponse signup(SignupRequest signupRequest) throws ApplicationException;
    OtpValidationResponse validateOtp(OtpValidationRequest otpValidationRequest) throws Exception;
    String resendEmail(OtpValidationRequest otpValidationRequest) throws Exception;
}
