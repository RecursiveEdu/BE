/**
 * Copyright (c) 2025 Recursive Education. All rights reserved.
 */
package com.recursive.edu.backend.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author PrantikGuha
 * CreatedAt: {13-11-2025}
 */
@Data
@Builder
public class OptionDTO {
    private String optionPublicId;
    private String optionText;
}
