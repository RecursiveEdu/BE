/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.service;

/**
 * @author PrantikGuha
 * CreatedAt: {27-10-2025}
 */
@FunctionalInterface
public interface ServiceExecutor<T> {
    T execute() throws Exception;
}
