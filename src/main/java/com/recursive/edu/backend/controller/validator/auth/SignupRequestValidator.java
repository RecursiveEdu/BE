/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.controller.validator.auth;

import com.recursive.edu.backend.controller.request.SignupRequest;
import com.recursive.edu.backend.util.StringHelper;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author PrantikGuha
 * CreatedAt: {06-11-2025}
 */
@Component("signupRequestValidator")
public class SignupRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return SignupRequestValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignupRequest command = (SignupRequest) target;
        if (StringHelper.isEmpty(command.getToken()) && command.getUserSignupRequest() == null) {
            if (StringHelper.isEmpty(command.getToken())) {
                errors.rejectValue("Token", "3", "is required");
            }
            if (command.getUserSignupRequest() == null) {
                errors.rejectValue("UserSignupRequest", "3", "is required");
            }
        }
        if (StringHelper.isEmpty(command.getToken()) && command.getUserSignupRequest() != null) {
            if (StringHelper.isEmpty(command.getUserSignupRequest().getFirstName())) {
                errors.rejectValue("FirstName", "3", "is required");
            }
            if (StringHelper.isEmpty(command.getUserSignupRequest().getLastName())) {
                errors.rejectValue("LastName", "3", "is required");
            }
            if (StringHelper.isEmpty(command.getUserSignupRequest().getEmail())) {
                errors.rejectValue("Email", "3", "is required");
            }
            if (StringHelper.isEmpty(command.getUserSignupRequest().getMobile())) {
                errors.rejectValue("Mobile", "3", "is required");
            }
        }
    }
}
