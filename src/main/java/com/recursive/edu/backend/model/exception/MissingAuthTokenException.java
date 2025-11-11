/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.model.exception;

import org.springframework.security.authentication.BadCredentialsException;

/**
 * @author PrantikGuha
 * CreatedAt: {10-11-2025}
 */
public class MissingAuthTokenException extends BadCredentialsException {
    public MissingAuthTokenException(String message) {
        super(message);
    }
}
