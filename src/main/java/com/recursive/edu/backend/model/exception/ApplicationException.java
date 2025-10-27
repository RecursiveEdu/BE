/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.model.exception;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
public class ApplicationException extends Exception {
    public ApplicationException(String message) {
        super(message);
    }
    public ApplicationException() {
        super("Unable to process the request, please try after sometime.");
    }
}
