/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.controller.validator.auth;

import com.recursive.edu.backend.controller.request.OtpValidationRequest;
import com.recursive.edu.backend.util.StringHelper;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author PrantikGuha
 * CreatedAt: {08-11-2025}
 */
@Component("otpRequestValidator")
public class OtpRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return OtpRequestValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        OtpValidationRequest command = (OtpValidationRequest) target;
        if (StringHelper.isEmpty(command.getOtp())) {
            errors.rejectValue("OTP", "3", "is required");
        }
        if (StringHelper.isEmpty(command.getId())) {
            errors.rejectValue("ID", "3", "is required");
        }
        if (command.getType() == null || StringHelper.isEmpty(command.getType().name())) {
            errors.rejectValue("Type", "3", "is required");
        }
    }
}
