package com.recursive.edu.backend.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author PrantikGuha
 * CreatedAt: {08-11-2025}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpValidationResponse {
    private boolean validated;
    private String code;
    private String message;
}
