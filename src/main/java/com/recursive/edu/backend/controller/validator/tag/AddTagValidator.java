package com.recursive.edu.backend.controller.validator.tag;

import com.recursive.edu.backend.controller.request.AddTagRequest;
import com.recursive.edu.backend.util.StringHelper;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author PrantikGuha
 * CreatedAt: {22-11-2025}
 */
@Component("addTagValidator")
public class AddTagValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return AddTagValidator.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AddTagRequest command = (AddTagRequest) target;
        if (StringHelper.isEmpty(command.getTag())) {
            errors.rejectValue("Tag", "3", "is required");
        }
    }
}
