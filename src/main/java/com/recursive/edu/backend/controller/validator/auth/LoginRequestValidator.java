/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.controller.validator.auth;

import com.recursive.edu.backend.controller.request.LoginRequest;
import com.recursive.edu.backend.util.StringHelper;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
@Component("loginRequestValidator")
public class LoginRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return LoginRequestValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        LoginRequest command = (LoginRequest) target;
        if (StringHelper.isEmpty(command.getEmail())) {
            errors.rejectValue("Email", "3", "is required");
        }
        if (StringHelper.isEmpty(command.getPassword())) {
            errors.rejectValue("Password", "3", "is required");
        }
    }
}
