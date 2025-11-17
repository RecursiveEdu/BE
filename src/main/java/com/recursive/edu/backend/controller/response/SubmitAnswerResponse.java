/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author PrantikGuha
 * CreatedAt: {13-11-2025}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitAnswerResponse {
    private int code;
    private String message;
}
