/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.helper;

import com.recursive.edu.backend.controller.BindingResultErrorCollector;
import com.recursive.edu.backend.model.common.ApiResult;
import com.recursive.edu.backend.model.common.Error;
import com.recursive.edu.backend.model.exception.ApplicationException;
import com.recursive.edu.backend.service.ServiceExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.Collections;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
@Component
public class ControllerHelper {

    private final BindingResultErrorCollector errorCollector;

    public ControllerHelper(BindingResultErrorCollector errorCollector) {
        this.errorCollector = errorCollector;
    }

    public <T, R> ResponseEntity<?> validateAndExecute(Validator validator, BindingResult bindingResult, R command, ServiceExecutor<T> executor) {
        validator.validate(command, bindingResult);
        if (bindingResult.hasErrors()) {
            return constructFieldErrorResponse(bindingResult);
        }
        return execute(executor);
    }

    public <T> ResponseEntity<?> execute(ServiceExecutor<T> executor) {
        try {
            T payload = executor.execute();
            return constructSuccessResponse(payload);
        } catch (ApplicationException applicationException) {
            return constructErrorResponse(applicationException);
        } catch (Exception exception) {
            return constructErrorResponse(exception);
        }
    }

    private <T> ResponseEntity<?> constructFieldErrorResponse(BindingResult bindingResult) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResult.newInstance()
                .withFiledErrors(errorCollector.getAllErrors(bindingResult)).withStatus("400").build());
    }

    private <T> ResponseEntity <?> constructSuccessResponse(T payload) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.newInstance()
                .withPayload(payload).withStatus("200").build());
    }

    private <T> ResponseEntity <?> constructErrorResponse(ApplicationException exp) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.newInstance()
                .withErrors(Collections.singletonList(Error.createInstance(exp.getMessage())))
                .withStatus("500").build());
    }

    private <T> ResponseEntity <?> constructErrorResponse(Exception exp) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResult.newInstance()
                .withErrors(Collections.singletonList(Error.createInstance(exp.getMessage())))
                .withStatus("500").build());
    }
}
