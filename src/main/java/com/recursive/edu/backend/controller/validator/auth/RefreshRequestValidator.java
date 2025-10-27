package com.recursive.edu.backend.controller.validator.auth;

import com.recursive.edu.backend.controller.request.AuthRefreshRequest;
import com.recursive.edu.backend.util.StringHelper;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
@Component("refreshRequestValidator")
public class RefreshRequestValidator  implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return RefreshRequestValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AuthRefreshRequest command = (AuthRefreshRequest) target;
        if (StringHelper.isEmpty(command.getRefreshToken())) {
            errors.rejectValue("RefreshToken", "3", "is required");
        }
    }
}
