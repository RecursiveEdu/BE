package com.recursive.edu.backend.controller.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author PrantikGuha
 * CreatedAt: {22-11-2025}
 */
@Data
@Builder
public class SimilarTags {
    private List<String> similarTags;
}
