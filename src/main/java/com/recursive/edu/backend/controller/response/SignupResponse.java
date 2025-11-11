/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.controller.response;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author PrantikGuha
 * CreatedAt: {06-11-2025}
 */
@Data
@NoArgsConstructor
public class SignupResponse extends LoginResponse{
    private String code;
    private String message;
    private String otpSendTo;

    public SignupResponse(String code, String message) {
        super(null, null, null);
        this.code = code;
        this.message = message;
    }

    public SignupResponse(String code, String message, String otpSendTo) {
        super(null, null, null);
        this.code = code;
        this.message = message;
        this.otpSendTo = otpSendTo;
    }

    public SignupResponse(String code, String message, String otpSendTo, LoginResponse loginResponse) {
        super(null, null, null);
        if (loginResponse != null) {
            super.setAccessToken(loginResponse.getAccessToken());
        }
        this.code = code;
        this.message = message;
        this.otpSendTo = otpSendTo;
    }

    public SignupResponse(String code, String message, LoginResponse loginResponse) {
        super(null, null, null);
        if (loginResponse != null) {
            super.setAccessToken(loginResponse.getAccessToken());
            super.setRefreshToken(loginResponse.getRefreshToken());
            super.setUser(loginResponse.getUser());
        }
        this.code = code;
        this.message = message;
    }
}
