/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.controller.request;

import com.recursive.edu.backend.constants.UserConstants;
import lombok.Data;

/**
 * @author PrantikGuha
 * CreatedAt: {08-11-2025}
 */
@Data
public class OtpValidationRequest {
    private String otp;
    private String id;
    private UserConstants.OtpType type;
}
