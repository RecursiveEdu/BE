/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.constants;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
public class UserConstants {
    public static final String NAME_FORMAT = "%s %s %s";
    public static final String EMAIL_OTP_FORMAT = "OTP:Email:%s";
    public static final String EMAIL_OTP_ATTEMPTS_FORMAT = "OTP:Email:%s-Attempts";
    public static final String EMAIL_OTP_TIME_FORMAT = "OTP:Email:%s-LastGeneratedTime";
    public static final String MOBILE_OTP_FORMAT = "OTP:Mobile:%s";
    public static final String MOBILE_OTP_ATTEMPTS_FORMAT = "OTP:Mobile:%s-Attempts";
    public static final String MOBILE_OTP_TIME_FORMAT = "OTP:Mobile:%s-LastGeneratedTime";
    public enum UserRole {
        PILOT
    }
    public enum OtpType {
        EMAIL, MOBILE, BOTH
    }
}
