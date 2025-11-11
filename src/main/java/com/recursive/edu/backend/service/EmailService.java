/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.service;

/**
 * @author PrantikGuha
 * CreatedAt: {06-11-2025}
 */
public interface EmailService {
    void sendOtpEmail(String name, String to, String otp, String expiryTime);
}
